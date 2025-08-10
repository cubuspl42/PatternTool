package diy.lingerie.web_tool_3d.application_state

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.Span
import dev.toolkt.math.algebra.linear.vectors.Vector2
import diy.lingerie.web_tool_3d.application_state.physics.Acceleration
import diy.lingerie.web_tool_3d.application_state.physics.Force
import diy.lingerie.web_tool_3d.application_state.physics.Mass
import diy.lingerie.web_tool_3d.application_state.physics.Velocity
import kotlin.time.Duration

class FabricPiece(
    val particleStateMap: ParticleStateMap,
    val springs: Set<Spring>,
) {
    value class ParticleStateMap(
        val particleStateById: Map<ParticleId, ParticleState>,
    ) {
        val states: Collection<ParticleState>
            get() = particleStateById.values

        fun getParticleState(
            particleId: ParticleId,
        ): ParticleState = particleStateById[particleId]
            ?: throw IllegalArgumentException("Particle with id $particleId not found in the particle state map.")

        fun applyForces(
            forceByParticleId: Map<ParticleId, Force>,
            stepDuration: Duration,
        ): ParticleStateMap = ParticleStateMap(
            particleStateById = particleStateById.mapValues { (particleId, particleState) ->
                val oldVelocity = particleState.velocity

                val newVelocity = when (val force = forceByParticleId[particleId]) {
                    null -> oldVelocity

                    else -> {
                        val acceleration = Acceleration.Companion.calculate(
                            force = force,
                            mass = ParticleState.mass,
                        )

                        oldVelocity.accelarate(
                            acceleration = acceleration,
                            duration = stepDuration,
                        )
                    }
                }

                val newPosition = Velocity.Companion.move(
                    point = particleState.position,
                    velocity = newVelocity,
                    duration = stepDuration,
                )

                ParticleState(
                    position = newPosition,
                    velocity = newVelocity,
                )
            },
        )
    }

    value class ParticleId(
        val id: Int,
    ) {
        companion object {
            private var nextId = 0

            fun next(): ParticleId = ParticleId(id = nextId++)
        }
    }

    data class ParticleState(
        val position: Point3D,
        val velocity: Velocity,
    ) {
        companion object {
            val mass: Mass = Mass(massValue = 0.001)

            fun stationary(
                position: Point3D,
            ): ParticleState = ParticleState(
                position = position,
                velocity = Velocity.Zero,
            )
        }
    }

    data class Spring(
        val restLength: Span,
        val firstParticleId: ParticleId,
        val secondParticleId: ParticleId,
    ) {
        data class ParticleCorrection(
            val particleId: ParticleId,
            val correctionForce: Force,
        )

        companion object {
            const val stretchCoefficient: Double = 0.9
        }

        fun correct(
            particleStateMap: ParticleStateMap,
        ): Iterable<ParticleCorrection> {
            val firstPosition = particleStateMap.getParticleState(
                particleId = firstParticleId,
            ).position

            val secondPosition = particleStateMap.getParticleState(
                particleId = secondParticleId,
            ).position

            val distance = Point3D.Companion.distanceBetween(firstPosition, secondPosition)

            // The correction force is positive if the particles are too far
            // and (attraction) negative if the particles are too close (repulsion)
            val correctionForceValue = stretchCoefficient * (distance.value - restLength.value)

            val firstDirection = firstPosition.directionTo(secondPosition)
            val secondDirection = firstDirection.opposite

            return listOf(
                ParticleCorrection(
                    particleId = firstParticleId,
                    correctionForce = Force.Companion.inDirection(
                        forceValue = correctionForceValue,
                        direction3 = firstDirection,
                    ),
                ),
                ParticleCorrection(
                    particleId = secondParticleId,
                    correctionForce = Force.Companion.inDirection(
                        forceValue = correctionForceValue,
                        direction3 = secondDirection,
                    ),
                ),
            )
        }
    }

    companion object {
        fun rectangular(
            /**
             * Width of the fabric piece in fabric pixel units (the number of
             * horizontal particles will equal [width] + 1)
             */
            width: Int,
            /**
             * Height of the fabric piece in fabric pixel units (the number of
             * vertical particles will equal [height] + 1)
             */
            height: Int,
            /**
             * The horizontal/vertical rest distance between the particles.
             */
            springRestLength: Span,
        ): FabricPiece {
            val n = width + 1
            val m = height + 1

            val spacingValue = springRestLength.value

            fun getParticleId(
                i: Int,
                j: Int,
            ): ParticleId = ParticleId(
                id = i * n + j,
            )

            val initialParticleStateById = (0 until m).flatMap { i ->
                val iD = i.toDouble()

                (0 until n).map { j ->
                    val jD = j.toDouble()

                    getParticleId(i = i, j = j) to ParticleState.stationary(
                        position = Point(
                            pointVector = Vector2(jD, iD) * spacingValue,
                        ).toPoint3D(),
                    )
                }
            }.toMap()

            val springs = (0 until m).flatMap { i ->
                val iNext = i + 1

                (1 until n).flatMap { j ->
                    val jNext = j + 1

                    val bottomSpring = when {
                        iNext < m -> Spring(
                            restLength = springRestLength,
                            firstParticleId = getParticleId(i = i, j = j),
                            secondParticleId = getParticleId(i = iNext, j = j),
                        )

                        else -> null
                    }

                    val rightSpring = when {
                        jNext < n -> Spring(
                            restLength = springRestLength,
                            firstParticleId = getParticleId(i = i, j = j),
                            secondParticleId = getParticleId(i = i, j = jNext),
                        )

                        else -> null
                    }

                    listOfNotNull(
                        bottomSpring,
                        rightSpring,
                    )
                }
            }.toSet()

            return FabricPiece(
                particleStateMap = ParticleStateMap(
                    particleStateById = initialParticleStateById,
                ),
                springs = springs,
            )
        }
    }

    val particleStates: Collection<ParticleState>
        get() = particleStateMap.states

    /**
     * Simulate the behavior of the fabric piece for a given [stepDuration] under the influence of an external force.
     */
    fun simulate(
        externalForce: Force,
        stepDuration: Duration,
    ): FabricPiece {
        val resultantForceByParticleId = springs.flatMap { spring ->
            spring.correct(
                particleStateMap = particleStateMap,
            )
        }.groupBy { it.particleId }.mapValues { (_, particleCorrection) ->
            Force.Companion.resultant(
                forces = particleCorrection.map { it.correctionForce } + externalForce,
            )
        }

        return FabricPiece(
            particleStateMap = particleStateMap.applyForces(
                forceByParticleId = resultantForceByParticleId,
                stepDuration = stepDuration,
            ),
            springs = springs,
        )
    }
}

#!/bin/bash

# Check if the task type argument is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <taskName>"
  exit 1
fi

TASK_NAME=$1

# Define the list of modules
MODULES=("core" "math" "geometry" "pureDom" "reactive" "reactiveDom")

# Define the color code for light blue
LIGHT_BLUE='\033[1;34m'

# Reset color
NC='\033[0m'

echo -e "${LIGHT_BLUE}Running task $TASK_NAME (root)${NC}"
./gradlew "${TASK_NAME}"
echo

# Change to the DevToolkt directory
cd ./DevToolkt || { echo "DevToolkt directory not found"; exit 1; }

# Run the specified task on each module
for MODULE in "${MODULES[@]}"; do
  echo -e "${LIGHT_BLUE}Running task $TASK_NAME in $MODULE${NC}"
  ./gradlew ":${MODULE}:${TASK_NAME}"
  echo
done

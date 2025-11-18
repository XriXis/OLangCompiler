#!/bin/bash

# Function to print the header
print_header() {
  echo -e "\n\033[1;34m====================================\033[0m"
  echo -e "\033[1;34m      WebAssembly Runner Script     \033[0m"
  echo -e "\033[1;34m====================================\033[0m"
}

# Function to display a fancy message in green
print_success() {
  echo -e "\033[1;32m$1\033[0m"
}

# Function to display an error message in red
print_error() {
  echo -e "\033[1;31m$1\033[0m"
}

# Function to display the user prompt
prompt_for_file() {
  echo -e "\n\033[1;33mPlease enter the path to your .wat file:\033[0m"
  read -r wat_file_path
}

# Function to check if the file exists
check_file_exists() {
  if [ ! -f "$1" ]; then
    print_error "Error: File '$1' does not exist."
    exit 1
  fi
}

# Main script starts here
print_header

# Get the file path from the user
prompt_for_file

# Check if the file exists
check_file_exists "$wat_file_path"

# Display a message that we are starting the conversion
echo -e "\033[1;34mConverting .wat file to .wasm...\033[0m"
wat2wasm "$wat_file_path" -o "${wat_file_path%.wat}.wasm"

# Check if the conversion was successful
if [ $? -eq 0 ]; then
  print_success "Conversion successful! Created: ${wat_file_path%.wat}.wasm"
else
  print_error "Conversion failed. Please check your .wat file."
  exit 1
fi

# Run the .wasm file with wasmer
echo -e "\033[1;34mRunning .wasm file with wasmer...\033[0m"
wasmer run "${wat_file_path%.wat}.wasm"

# Check if wasmer ran successfully
if [ $? -eq 0 ]; then
  print_success "WebAssembly execution completed successfully."
else
  print_error "WebAssembly execution failed. Please check your environment."
  exit 1
fi

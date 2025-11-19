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

# Function to display a neutral message in yellow
print_info() {
  echo -e "\033[1;33m$1\033[0m"
}

# Function to check if the file exists
check_file_exists() {
  if [ ! -f "$1" ]; then
    print_error "Error: File '$1' does not exist."
    exit 1
  fi
}

# Function to handle conversion and execution
convert_and_run_wasm() {
  # Check if the file exists
  check_file_exists "$wat_file_path"

  formatted_file="${wat_file_path%.wat}_formatted.wat"
  # Display a message that we are starting the conversion
  print_info "Formatting..."
  wasm-tools parse "$wat_file_path" -t -o "$formatted_file"
  print_info "Converting .wat file to .wasm..."
  wat2wasm "$wat_file_path" -o "${wat_file_path%.wat}.wasm"
#  wat2wasm "$formatted_file" -o "${wat_file_path%.wat}.wasm"
  # Check if the conversion was successful
  if [ $? -eq 0 ]; then
    print_success "Conversion successful! Created: ${wat_file_path%.wat}.wasm"
  else
    print_error "Conversion failed. Please check your .wat file."
    exit 1
  fi

  # Run the .wasm file with wasmer
  print_info "Running .wasm file with wasmer..."
  wasmer run "${wat_file_path%.wat}.wasm"
#  wasmer run "${formatted_file%.wat}.wasm"

  # Check if wasmer ran successfully
  if [ $? -eq 0 ]; then
    print_success "WebAssembly execution completed successfully."
  else
    print_error "WebAssembly execution failed. Please check your environment."
    exit 1
  fi
}

# Main script starts here
print_header

# Check if a path is provided as a command-line argument
if [ $# -eq 0 ]; then
  print_error "Error: No file path provided. Please provide the path to your .wat file."
  exit 1
fi

# Get the file path from the first command-line argument
wat_file_path=$1

# Execute the conversion and running process
convert_and_run_wasm

# Add a closing line separator
echo -e "\033[1;34m====================================\033[0m"

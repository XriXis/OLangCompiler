wat_file_path=$1
wasm-tools parse "$wat_file_path" -t -o "${wat_file_path%.wat}_formatted.wat"

#./data/format_wat.sh output/out.wat

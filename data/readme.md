## How to run it?

1. Generate / Paste .wat file
2. Compile it to .wasm
```shell
wat2wasm test.wat -o test.wasm
```
3. Execute .wasm file
```shell
wasmer run test.wasm 
```
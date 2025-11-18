## How to run it?

1. Generate / Paste .wat file
2. If you want to observe generated code, you can format it 
    ```shell
    wasm-tools parse test_generated.wat -t -o test.wat
    ```
3. Compile it to .wasm
    ```shell 
    wat2wasm test.wat -o test.wasm
    ```
4. Execute .wasm file
    ```shell
    wasmer run test.wasm 
    ```

**OR**

```shell
cd ..
./data/run_wasm.sh
```
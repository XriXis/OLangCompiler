import { readFile } from 'fs/promises';
import wabt from 'wabt';

async function runWat(fileToExecute) {
    try {
        // Читаем WAT файл
        const watCode = await readFile(fileToExecute, 'utf8');

        // Инициализируем wabt
        const wabtInstance = await wabt();

        // Парсим WAT и компилируем в WASM
        const parsed = wabtInstance.parseWat('test.wat', watCode);
        const { buffer } = parsed.toBinary({});

        // Импортируемые функции
        const importObject = {
            env: {
                memory: new WebAssembly.Memory({ initial: 1 }),
                printNumber: (value) => {
                    console.log('Integer:', value);
                },
                printString: (ptr) => {
                    const memory = new Uint8Array(importObject.env.memory.buffer);
                    let str = '';
                    for (let i = ptr; memory[i] !== 0; i++) {
                        str += String.fromCharCode(memory[i]);
                    }
                    console.log(str);
                }
            }
        };

        // Компилируем и запускаем WASM
        const module = await WebAssembly.instantiate(buffer, importObject);
        module.instance.exports._start();

    } catch (error) {
        console.error('Error:', error);
    }
}

// Получаем аргумент из командной строки
const fileToExecute = process.argv[2];
if (!fileToExecute) {
    console.error('Please provide a WAT file as argument');
    process.exit(1);
}

runWat(fileToExecute);

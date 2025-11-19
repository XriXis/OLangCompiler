(module
  ;; Импорт функций вывода из JavaScript
  (import "env" "printNumber" (func $printNumber (param i32)))
  (import "env" "printString" (func $printString (param i32)))

  (memory $0 1)

  (global $top_mem (mut i32) (i32.const 0))
  (func $malloc (param $alloc_size i32) (result i32)
    (local $res i32)
    (local.set $res (global.get $top_mem))
    (global.set
      $top_mem
      (i32.add
        (local.get $alloc_size)
        (global.get $top_mem)
      )
    )
    local.get $res
  )

  ;; start merge
  ;; class Console

  ;; Конструктор Console
  (func $Console_this_ (result i32)
    (call $malloc (i32.const 0))
  )

  ;; Метод print для Integer
  (func $Console_print_Integer (param $this i32) (param $i i32)
    ;; Вызываем импортированную функцию для вывода числа
    (call $printNumber (local.get $i))
  )

  ;; Метод println для Integer
  (func $Console_println_Integer (param $this i32) (param $i i32)
    ;; Выводим число
    (call $Console_print_Integer (local.get $this) (local.get $i))
    ;; Выводим новую строку (через вывод строки "\n")
    (call $printString (i32.const 256)) ;; адрес строки "\n" в памяти
  )

  ;; Строковые константы в памяти
  (data (i32.const 256) "\0A\00") ;; "\n" в формате null-terminated

  ;; end merge

  (func $Main_this_ (export "_start") (result i32)
      (local $a i32)
      (local $b i32)
      (local $result i32)

      ;; Создаем true
      (i32.const 1)
      (local.set $a)

      (call $Console_println_Integer (i32.const 0) (local.get $a))
    )
)
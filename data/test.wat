(module
  (memory (export "memory") 1)

  ;; Глобальные переменные: смещения полей
  (global $offset_a i32 (i32.const 0))
  (global $offset_b i32 (i32.const 8))
  (global $size_MyClass i32 (i32.const 8))

  ;; Глобальная переменная для указателя на кучу памяти
  (global $heapPtr (mut i32) (i32.const 8))

  ;; Конструктор для MyClass
  (func $MyClass_new (result i32)
    (local $ptr i32)

    ;; Выделить память для нового объекта
    global.get $heapPtr
    local.set $ptr

    ;; Увеличить указатель кучи
    global.get $heapPtr
    global.get $size_MyClass
    i32.add
    global.set $heapPtr

    ;; Записать поле 'a' = 10
    local.get $ptr
    global.get $offset_a
    i32.add
    i64.const 10
    i64.store

    ;; Записать поле 'b' = 20
    local.get $ptr
    global.get $offset_b
    i32.add
    i64.const 20
    i64.store

    ;; Вернуть указатель на объект
    local.get $ptr
  )

  ;; Метод add()
  (func $MyClass_add (param $this i32) (result i64)
    ;; Загрузка значения поля 'a'
    local.get $this
    global.get $offset_a
    i32.add
    i64.load

    ;; Загрузка значения поля 'b'
    local.get $this
    global.get $offset_b
    i32.add
    i64.load

    ;; Возвращаем сумму полей 'a' и 'b'
    i64.add
  )

  ;; Точка входа
  (func $Main_this (export "_start") (result i64)
    (local $obj i32)
    (local $result i64)

    ;; Создание объекта MyClass
    call $MyClass_new
    local.set $obj

    ;; Вызов метода add()
    local.get $obj
    call $MyClass_add
    local.set $result

    ;; Возврат результата
    local.get $result
  )
)
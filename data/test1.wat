(module
  ;; Тип для виртуальных методов (функции с одним параметром - объект)
  (type $fnIntThis (func (param i32) (result i32)))

  ;; Таблица виртуальных методов для MyClass
  (table $vtable 1 funcref)

  ;; Заполнение таблицы методов
  (elem (i32.const 0) $MyClass_add_impl)

  (memory (export "memory") 1)

  ;; Глобальные переменные: смещения полей в объекте MyClass
  (global $offset_vtable (mut i32) (i32.const 0))  ;; Смещение для vtable
  (global $offset_a      (mut i32) (i32.const 4))  ;; Смещение для поля 'a'
  (global $offset_b      (mut i32) (i32.const 8))  ;; Смещение для поля 'b'

  ;; Размер объекта MyClass
  (global $size_MyClass (mut i32) (i32.const 12))  ;; 4 байта для vtable, 4 для 'a', 4 для 'b'

  ;; Определение конструктора для MyClass
  (func $MyClass_new (export "MyClass_new") (result i32)
    ;; Выделение памяти для объекта (12 байт)
    (local $ptr i32)
    ;; Инициализация указателя на память
    (i32.store (i32.const 0) (i32.const 0))

    ;; Выделить память для нового объекта
    (local.set $ptr (i32.add (i32.const 0) (i32.const 12)))
    ;; Записать указатель на vtable (index 0)
    local.get $ptr
    i32.const 0
    i32.store

    ;; Записать поле 'a' = 10
    local.get $ptr
    global.get $offset_a
    i32.add
    i32.const 10
    i32.store

    ;; Записать поле 'b' = 20
    local.get $ptr
    global.get $offset_b
    i32.add
    i32.const 20
    i32.store

    ;; Вернуть указатель на объект
    local.get $ptr
  )

  ;; Реализация метода add() для MyClass
  (func $MyClass_add_impl (type $fnIntThis) (param $this i32) (result i32)
    ;; Загрузка значения поля 'a'
    local.get $this
    global.get $offset_a
    i32.add
    i32.load

    ;; Загрузка значения поля 'b'
    local.get $this
    global.get $offset_b
    i32.add
    i32.load

    ;; Возвращаем сумму полей 'a' и 'b'
    i32.add
  )

  ;; Виртуальная функция для динамического вызова add() через vtable
  (func $MyClass_add (export "MyClass_add") (param $this i32) (result i32)
    ;; Загрузка индекса vtable для метода add()
    local.get $this
    global.get $offset_vtable
    i32.add
    i32.load    ;; Загрузка метода из таблицы vtable

    local.get $this  ;; Передача аргумента
    call_indirect (type $fnIntThis)
  )

    ;; Конструктор Main (точка входа)
    (func $Main_constructor (export "Main_constructor") (result i32)
      ;; Объявление всех локальных переменных в начале функции
      (local $obj i32)
      (local $result i32)

      ;; Создание объекта MyClass
      (call $MyClass_new)
      (local.set $obj)

      ;; Вызов метода add() и сохранение результата в $result
      (local.get $obj)
      (call $MyClass_add)
      (local.set $result)

      ;; Печать результата
      (local.get $result)
    )

  ;; Точка входа: вызов конструктора Main
  (export "_start" (func $Main_constructor))

  ;; Глобальная переменная для указателя на кучу памяти
  (global $heapPtr (mut i32) (i32.const 1024))
)
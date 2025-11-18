(module
  ;; ==================== ПАМЯТЬ И СТРУКТУРА ====================
  (memory (export "memory") 1)

  ;; Смещения полей класса Integer
  (global $Integer_vtable_offset i32 (i32.const 0))  ;; указатель на vtable
  (global $Integer_value_offset i32 (i32.const 4))   ;; значение (i32)
  (global $Integer_size i32 (i32.const 8))           ;; общий размер

  ;; Указатель кучи
  (global $heap_ptr (mut i32) (i32.const 1024))

  ;; Таблица виртуальных методов (объявлена ДО elem)
  (table $vtable 4 funcref)

  ;; Инициализация таблицы (после объявления таблицы)
  (elem (i32.const 0) $Integer_getValue_impl $Integer_setValue_impl $Integer_toString_impl $Integer_add_impl)

  ;; Типы методов
  (type $getValue_type (func (param i32) (result i32)))
  (type $setValue_type (func (param i32) (param i32)))
  (type $toString_type (func (param i32) (result i32)))
  (type $add_type (func (param i32) (param i32) (result i32)))

  ;; ==================== РЕАЛИЗАЦИЯ МЕТОДОВ ====================

  ;; Конструктор
  (func $Integer_new (export "Integer_new") (param $initial_value i32) (result i32)
    (local $this i32)

    ;; Выделяем память
    global.get $heap_ptr
    local.set $this

    ;; Увеличиваем указатель кучи
    global.get $heap_ptr
    global.get $Integer_size
    i32.add
    global.set $heap_ptr

    ;; Инициализируем vtable (индекс 0 в таблице)
    local.get $this
    global.get $Integer_vtable_offset
    i32.add
    i32.const 0  ;; базовый индекс методов Integer в таблице
    i32.store

    ;; Устанавливаем начальное значение
    local.get $this
    global.get $Integer_value_offset
    i32.add
    local.get $initial_value
    i32.store

    local.get $this
  )

  ;; GetValue - возвращает значение
  (func $Integer_getValue_impl (type $getValue_type) (param $this i32) (result i32)
    local.get $this
    global.get $Integer_value_offset
    i32.add
    i32.load
  )

  (func $Integer_getValue (export "Integer_getValue") (param $this i32) (result i32)
    ;; Виртуальный вызов через vtable - индекс 0 для getValue
    local.get $this
    i32.load  ;; загружаем базовый индекс vtable
    i32.const 0  ;; смещение для метода getValue
    i32.add
    local.get $this
    call_indirect (type $getValue_type)
  )

  ;; SetValue - устанавливает значение
  (func $Integer_setValue_impl (type $setValue_type) (param $this i32) (param $new_value i32)
    local.get $this
    global.get $Integer_value_offset
    i32.add
    local.get $new_value
    i32.store
  )

  (func $Integer_setValue (export "Integer_setValue") (param $this i32) (param $new_value i32)
    ;; Виртуальный вызов через vtable - индекс 1 для setValue
    local.get $this
    i32.load  ;; базовый индекс
    i32.const 1  ;; смещение для метода setValue
    i32.add
    local.get $this
    local.get $new_value
    call_indirect (type $setValue_type)
  )

  ;; ToString - преобразует в строку (упрощенная версия)
  (func $Integer_toString_impl (type $toString_type) (param $this i32) (result i32)
    ;; В реальной реализации здесь была бы конвертация числа в строку
    ;; Для простоты возвращаем указатель на статическую строку
    i32.const 256  ;; адрес строки в памяти
  )

  (func $Integer_toString (export "Integer_toString") (param $this i32) (result i32)
    ;; Виртуальный вызов через vtable - индекс 2 для toString
    local.get $this
    i32.load  ;; базовый индекс
    i32.const 2  ;; смещение для метода toString
    i32.add
    local.get $this
    call_indirect (type $toString_type)
  )

  ;; Add - складывает с другим Integer
  (func $Integer_add_impl (type $add_type) (param $this i32) (param $other i32) (result i32)
    (local $result i32)

    ;; Получаем значение текущего объекта
    local.get $this
    global.get $Integer_value_offset
    i32.add
    i32.load

    ;; Получаем значение другого объекта
    local.get $other
    global.get $Integer_value_offset
    i32.add
    i32.load

    ;; Складываем
    i32.add
    local.set $result

    ;; Создаем новый Integer с результатом
    local.get $result
    call $Integer_new
  )

  (func $Integer_add (export "Integer_add") (param $this i32) (param $other i32) (result i32)
    ;; Виртуальный вызов через vtable - индекс 3 для add
    local.get $this
    i32.load  ;; базовый индекс
    i32.const 3  ;; смещение для метода add
    i32.add
    local.get $this
    local.get $other
    call_indirect (type $add_type)
  )

  ;; ==================== ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ====================

  ;; Статический метод для создания Integer из значения
  (func $Integer_valueOf (export "Integer_valueOf") (param $value i32) (result i32)
    local.get $value
    call $Integer_new
  )

  ;; Сравнение двух Integer
  (func $Integer_equals (export "Integer_equals") (param $this i32) (param $other i32) (result i32)
    ;; Получаем значение текущего объекта
    local.get $this
    call $Integer_getValue

    ;; Получаем значение другого объекта
    local.get $other
    call $Integer_getValue

    ;; Сравниваем
    i32.eq
  )

  ;; ==================== ТЕСТОВАЯ ТОЧКА ВХОДА ====================
  (func $_start (export "_start") (result i32)
    (local $a i32)
    (local $b i32)
    (local $c i32)

    ;; Создаем Integer a = 10
    i32.const 10
    call $Integer_new
    local.set $a

    ;; Создаем Integer b = 20
    i32.const 20
    call $Integer_new
    local.set $b

    ;; a + b
    local.get $a
    local.get $b
    call $Integer_add
    local.set $c

    ;; Возвращаем результат (должно быть 30)
    local.get $c
    call $Integer_getValue
  )

  ;; Инициализация строковых констант
  (data (i32.const 256) "Integer\00")
)
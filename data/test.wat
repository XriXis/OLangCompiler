(module
  ;; ==================== ПАМЯТЬ И АЛЛОКАТОР ====================
  (memory 1)
  (global $heap_ptr (mut i32) (i32.const 1024))

  (func $malloc (param $size i32) (result i32)
    (local $ptr i32)
    (global.get $heap_ptr)
    (local.set $ptr)
    (global.set $heap_ptr (i32.add (global.get $heap_ptr) (local.get $size)))
    (local.get $ptr)
  )

  ;; ==================== ТАБЛИЦА ВИРТУАЛЬНЫХ МЕТОДОВ ====================
  ;; 3 класса × 2 метода = 6 методов
  (table $vtable 6 funcref)
  ;; Распределение:
  ;; Animal: speak (0), move (1)
  ;; Dog:    speak (2), move (3)
  ;; Cat:    speak (4), move (5)
  (elem (i32.const 0) funcref
    (ref.func $Animal_speak_impl)  ;; 0
    (ref.func $Animal_move_impl)   ;; 1
    (ref.func $Dog_speak_impl)     ;; 2
    (ref.func $Dog_move_impl)      ;; 3
    (ref.func $Cat_speak_impl)     ;; 4
    (ref.func $Cat_move_impl)      ;; 5
  )

  ;; Типы методов (теперь move принимает параметр!)
  (type $Speak (func (param i32) (result i32)))
  (type $Move (func (param i32) (param i32) (result i32)))  ;; добавили параметр!

  ;; ==================== КЛАСС Animal ====================
  (global $Animal_size i32 (i32.const 8))

  ;; Реализация методов Animal
  (func $Animal_speak_impl (type $Speak) (param $this i32) (result i32)
    ;; Animal.speak возвращает 10
    (i32.const 10)
  )

  (func $Animal_move_impl (type $Move) (param $this i32) (param $steps i32) (result i32)
    ;; Animal.move(steps) возвращает steps * 1
    (i32.mul (local.get $steps) (i32.const 1))
  )

  ;; Конструктор Animal
  (func $Animal_this_ (result i32)
    (local $this i32)
    (local.set $this (call $malloc (global.get $Animal_size)))

    ;; Animal хранит базовый индекс 0 в таблице методов
    (i32.store (local.get $this) (i32.const 0))

    (local.get $this)
  )

  ;; Виртуальные методы Animal
  (func $Animal_speak (param $this i32) (result i32)
    (local $base_index i32)
    (local.set $base_index (i32.load (local.get $this)))

    ;; speak всегда по смещению 0 от базового индекса
    (call_indirect (type $Speak)
      (local.get $this)
      (local.get $base_index)
    )
  )

  ;; Move теперь принимает параметр steps
  (func $Animal_move (param $this i32) (param $steps i32) (result i32)
    (local $base_index i32)
    (local.set $base_index (i32.load (local.get $this)))

    ;; move всегда по смещению +1 от базового индекса
    (call_indirect (type $Move)
      (local.get $this) (local.get $steps)  ;; передаем оба параметра
      (i32.add (local.get $base_index) (i32.const 1))
    )
  )

  ;; ==================== КЛАСС Dog ====================
  (global $Dog_size i32 (i32.const 8))

  ;; Реализация методов Dog
  (func $Dog_speak_impl (type $Speak) (param $this i32) (result i32)
    ;; Dog.speak возвращает 5
    (i32.const 5)
  )

  (func $Dog_move_impl (type $Move) (param $this i32) (param $steps i32) (result i32)
    ;; Dog.move(steps) возвращает steps * 3
    (i32.mul (local.get $steps) (i32.const 3))
  )

  ;; Конструктор Dog
  (func $Dog_this_ (result i32)
    (local $this i32)
    (local.set $this (call $malloc (global.get $Dog_size)))

    ;; Dog хранит базовый индекс 2 в таблице методов
    (i32.store (local.get $this) (i32.const 2))

    (local.get $this)
  )

  ;; ==================== КЛАСС Cat ====================
  (global $Cat_size i32 (i32.const 8))

  ;; Реализация методов Cat
  (func $Cat_speak_impl (type $Speak) (param $this i32) (result i32)
    ;; Cat.speak возвращает 2
    (i32.const 2)
  )

  (func $Cat_move_impl (type $Move) (param $this i32) (param $steps i32) (result i32)
    ;; Cat.move(steps) возвращает steps * 2
    (i32.mul (local.get $steps) (i32.const 2))
  )

  ;; Конструктор Cat
  (func $Cat_this_ (result i32)
    (local $this i32)
    (local.set $this (call $malloc (global.get $Cat_size)))

    ;; Cat хранит базовый индекс 4 в таблице методов
    (i32.store (local.get $this) (i32.const 4))

    (local.get $this)
  )

  ;; ==================== КЛАСС Console ====================
  (import "env" "printNumber" (func $printNumber (param i32)))
  (import "env" "printString" (func $printString (param i32)))

  (func $Console_this_ (result i32)
    (call $malloc (i32.const 4))
  )

  (func $Console_println_Integer (param $this i32) (param $num i32)
    (call $printNumber (local.get $num))
    (call $printString (i32.const 256))  ;; "\n"
  )

  ;; ==================== КЛАСС Main ====================
  (global $Main_size i32 (i32.const 4))

  ;; Конструктор Main (теперь с параметром для move)
  (func $Main_this_ (result i32)
    (local $this i32)
    (local $dog i32)
    (local $cat i32)

    ;; Создаем объект Main
    (local.set $this (call $malloc (global.get $Main_size)))

    ;; Создаем Dog и Cat
    (local.set $dog (call $Dog_this_))
    (local.set $cat (call $Cat_this_))

    ;; Вызываем saySpeak для dog и cat
    (local.get $this)
    (local.get $dog)
    (call $Main_saySpeak)

    (local.get $this)
    (local.get $cat)
    (call $Main_saySpeak)

    ;; Вызываем sayMove для dog и cat с параметром
    (local.get $this)
    (local.get $dog)
    (i32.const 4)  ;; steps = 4
    (call $Main_sayMove)

    (local.get $this)
    (local.get $cat)
    (i32.const 3)  ;; steps = 3
    (call $Main_sayMove)

    (local.get $this)
  )

  ;; Метод saySpeak (без изменений)
  (func $Main_saySpeak (param $this i32) (param $animal i32)
    (local $console i32)
    (local $num i32)

    ;; num := animal.speak()
    (local.set $num (call $Animal_speak (local.get $animal)))

    ;; Console().println(num)
    (local.set $console (call $Console_this_))
    (local.get $console)
    (local.get $num)
    (call $Console_println_Integer)
  )

  ;; Метод sayMove теперь принимает steps параметр
  (func $Main_sayMove (param $this i32) (param $animal i32) (param $steps i32)
    (local $console i32)
    (local $result i32)

    ;; result := animal.move(steps) - передаем параметр!
    (local.set $result
      (call $Animal_move (local.get $animal) (local.get $steps))
    )

    ;; Console().println(result)
    (local.set $console (call $Console_this_))
    (local.get $console)
    (local.get $result)
    (call $Console_println_Integer)
  )

  ;; ==================== ТЕСТОВАЯ ФУНКЦИЯ ====================
  ;; Дополнительный тест с разными значениями steps
  (func $test_with_different_steps (result i32)
    (local $dog i32)
    (local $cat i32)

    (local.set $dog (call $Dog_this_))
    (local.set $cat (call $Cat_this_))

    ;; Dog.move(1) = 1 * 3 = 3
    (call $Animal_move (local.get $dog) (i32.const 1))
    ;; Dog.move(2) = 2 * 3 = 6
    (call $Animal_move (local.get $dog) (i32.const 2))
    ;; Dog.move(5) = 5 * 3 = 15
    (call $Animal_move (local.get $dog) (i32.const 5))

    ;; Cat.move(1) = 1 * 2 = 2
    (call $Animal_move (local.get $cat) (i32.const 1))
    ;; Cat.move(4) = 4 * 2 = 8
    (call $Animal_move (local.get $cat) (i32.const 4))

    (i32.const 0)
  )

  ;; ==================== ТОЧКА ВХОДА ====================
  (func $_start (export "_start") (result i32)
    ;; Создаем Main (в конструкторе уже все выполняется)
    (call $Main_this_)
    (drop)

    ;; Дополнительный тест
    (call $test_with_different_steps)
    (drop)

    (i32.const 0)
  )

  ;; Строковые константы
  (data (i32.const 256) "\0A\00")  ;; "\n"
)
;; перед запуском вставить тип Integer!!!
(module
  ;; ==================== ПАМЯТЬ И СТРУКТУРА ====================
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

  ;; ==================== Integer ====================
  ;; class Integer
    (global $Integer_value_offset i32 (i32.const 0))        ;; основное значение (i32)
    (global $Integer_Min_Integer_offset i32 (i32.const 4))  ;; min value
    (global $Integer_Max_Integer_offset i32 (i32.const 8))  ;; max value
    (global $Integer_size i32 (i32.const 12))               ;; общий размер: 12 байт

    ;; Константы для минимального и максимального значения Integer
    (global $INTEGER_MIN i32 (i32.const -2147483648))   ;; -2^31
    (global $INTEGER_MAX i32 (i32.const 2147483647))    ;; 2^31 - 1

    ;; constructor from int $Integer_this_Integer
    ;; TODO: like Integer(Integer(5)) or just Integer(5) ???
    ;; $Integer_this_Integer
    (func $Integer_this_Integer (param $p i32) (result i32)
      (local $this i32)
      (local.set $this (call $malloc (global.get $Integer_size))) ;; malloc mem
      ;; Инициализируем основное значение (по умолчанию 0)
      (i32.store
        (i32.add (local.get $this) (global.get $Integer_value_offset))
        (local.get $p)
      )

      ;; Инициализируем min_value константой
      (i32.store
        (i32.add (local.get $this) (global.get $Integer_Min_Integer_offset))
        (global.get $INTEGER_MIN)
      )

      ;; Инициализируем max_value константой
      (i32.store
        (i32.add (local.get $this) (global.get $Integer_Max_Integer_offset))
        (global.get $INTEGER_MAX)
      )

      (local.get $this)
    )

    ;; constructor from Real
    ;; $Integer_this_Real
    (func $Integer_this_Real (param $p f32) (result i32)
      ;; Конвертируем Real в Integer (округляем к 0)
      (call $Integer_this_Integer (i32.trunc_f32_s (local.get $p)))
    )

    ;; emtpy constructor, set value = 0
    ;; $Integer_this_
    (func $Integer_this_ (result i32)
      (local $this i32)
      (local.set $this (call $malloc (global.get $Integer_size))) ;; malloc mem
      ;; Инициализируем основное значение (по умолчанию 0)
      (i32.store
        (i32.add (local.get $this) (global.get $Integer_value_offset))
        (i32.const 0)
      )

      ;; Инициализируем min_value константой
      (i32.store
        (i32.add (local.get $this) (global.get $Integer_Min_Integer_offset))
        (global.get $INTEGER_MIN)
      )

      ;; Инициализируем max_value константой
      (i32.store
        (i32.add (local.get $this) (global.get $Integer_Max_Integer_offset))
        (global.get $INTEGER_MAX)
      )

      (local.get $this)
    )

    ;; TODO: $Integer_ToString_
    (func $Integer_ToString_ (param $this i32) (result i32)
      (i32.const 0)
    )

    ;; $Integer_Get_
    (func $Integer_Get_ (param $this i32) (result i32)
      (i32.load
        (i32.add (local.get $this) (global.get $Integer_value_offset))
      )
    )


    ;; TODO: $Integer_toReal_
    (func $Integer_toReal_ (param $this i32) (result f32)

        (f32.const 0.0)
    )

    ;; TODO: $Integer_toBoolean_
    (func $Integer_toBoolean_ (param $this i32) (result i32)

        (i32.const 0)
    )

    ;; $Integer_UnaryMinus_
    (func $Integer_UnaryMinus_ (param $this i32) (result i32)
      (local $value i32)
      (local $result i32)

      ;; Получаем текущее значение
      (local.set $value
        (i32.load
          (i32.add (local.get $this) (global.get $Integer_value_offset))
        )
      )

      ;; Вычисляем -value (особый случай для MIN_VALUE)
      (if (i32.eq (local.get $value) (global.get $INTEGER_MIN))
        (then
          ;; Для -2147483648 унарный минус вернет то же значение (переполнение)
          (local.set $result (global.get $INTEGER_MIN))
        )
        (else
          (local.set $result (i32.sub (i32.const 0) (local.get $value)))
        )
      )

      ;; Создаем новый Integer с результатом
      (call $Integer_this_Integer (local.get $result))
    )

    ;; $Integer_Plus_Integer
    (func $Integer_Plus_Integer (param $this i32) (param $p i32) (result i32)
      (local $value1 i32)
      (local $value2 i32)
      (local $result i32)

      ;; Получаем значение текущего объекта
      (local.set $value1
        (i32.load
          (i32.add (local.get $this) (global.get $Integer_value_offset))
        )
      )

      ;; Получаем значение другого объекта
      (local.set $value2
        (i32.load
          (i32.add (local.get $p) (global.get $Integer_value_offset))
        )
      )

      ;; Выполняем сложение
      (local.set $result (i32.add (local.get $value1) (local.get $value2)))

  ;;    ;; Проверка на переполнение (упрощенная)
  ;;    (if (i32.and
  ;;          (i32.xor (local.get $value1) (local.get $value2))  ;; если знаки разные - переполнения нет
  ;;          (i32.xor (local.get $value1) (local.get $result))  ;; если знак результата отличается от value1
  ;;        )
  ;;      (then
  ;;        ;; Произошло переполнение - возвращаем граничное значение
  ;;        (if (i32.lt_s (local.get $value1) (i32.const 0))
  ;;          (then
  ;;            (local.set $result (global.get $INTEGER_MIN))  ;; отрицательное переполнение
  ;;          )
  ;;          (else
  ;;            (local.set $result (global.get $INTEGER_MAX))  ;; положительное переполнение
  ;;          )
  ;;        )
  ;;      )
  ;;      (else
  ;;      )
  ;;    )

      ;; Создаем новый Integer с результатом
      (call $Integer_this_Integer (local.get $result))
    )

    ;; TODO: $Integer_Plus_Real
    (func $Integer_Plus_Real (param $this i32) (param $p f32) (result f32)

        (f32.const 0.0)
    )

    ;; $Integer_Minus_Integer
    (func $Integer_Minus_Integer (param $this i32) (param $p i32) (result i32)
      (local $value1 i32)
      (local $value2 i32)
      (local $result i32)

      ;; Получаем значения
      (local.set $value1
        (i32.load
          (i32.add (local.get $this) (global.get $Integer_value_offset))
        )
      )
      (local.set $value2
        (i32.load
          (i32.add (local.get $p) (global.get $Integer_value_offset))
        )
      )

      ;; Выполняем вычитание: value1 - value2
      (local.set $result (i32.sub (local.get $value1) (local.get $value2)))

      ;; Проверка на переполнение (аналогично сложению)
      (if (i32.and
            (i32.eq (i32.xor (local.get $value1) (local.get $value2)) (i32.const 0x80000000))  ;; разные знаки
            (i32.xor (local.get $value1) (local.get $result))  ;; знак результата отличается
          )
        (then
          (if (i32.lt_s (local.get $value1) (i32.const 0))
            (then (local.set $result (global.get $INTEGER_MIN)))
            (else (local.set $result (global.get $INTEGER_MAX)))
          )
        )
        (else
        )
      )

      (call $Integer_this_Integer (local.get $result))
    )

    ;; TODO: $Integer_Minus_Real
    (func $Integer_Minus_Real (param $this i32) (param $p f32) (result f32)

        (f32.const 0.0)
    )

    ;; $Integer_Mult_Integer
    (func $Integer_Mult_Integer (param $this i32) (param $p i32) (result i32)
      (local $value1 i32)
      (local $value2 i32)
      (local $result i32)

      ;; Получаем значения
      (local.set $value1
        (i32.load
          (i32.add (local.get $this) (global.get $Integer_value_offset))
        )
      )
      (local.set $value2
        (i32.load
          (i32.add (local.get $p) (global.get $Integer_value_offset))
        )
      )

      ;; Выполняем умножение
      (local.set $result (i32.mul (local.get $value1) (local.get $value2)))

      ;; Упрощенная проверка переполнения (можно добавить позже)
      ;; Пока просто возвращаем результат

      ;; Создаем новый Integer с результатом
      (call $Integer_this_Integer (local.get $result))
    )

    ;; TODO: $Integer_Mult_Real
    (func $Integer_Mult_Real (param $this i32) (param $p f32) (result f32)

        (f32.const 0.0)
    )

    ;; $Integer_Div_Integer
    (func $Integer_Div_Integer (param $this i32) (param $p i32) (result i32)
      (local $value1 i32)
      (local $value2 i32)
      (local $result i32)

      ;; Получаем значения
      (local.set $value1
        (i32.load
          (i32.add (local.get $this) (global.get $Integer_value_offset))
        )
      )
      (local.set $value2
        (i32.load
          (i32.add (local.get $p) (global.get $Integer_value_offset))
        )
      )

      ;; Проверка деления на ноль
      (if (i32.eq (local.get $value2) (i32.const 0))
        (then
          ;; Деление на ноль - возвращаем 0 (или можно вернуть MAX/MIN)
          (local.set $result (i32.const 0))
        )
        (else
          ;; Выполняем целочисленное деление
          (local.set $result (i32.div_s (local.get $value1) (local.get $value2)))
        )
      )

      ;; Создаем новый Integer с результатом
      (call $Integer_this_Integer (local.get $result))
    )

    ;; TODO: $Integer_Div_Real
    (func $Integer_Div_Real (param $this i32) (param $p f32) (result f32)

        (f32.const 0.0)
    )

    ;; $Integer_Rem_Integer
    (func $Integer_Rem_Integer (param $this i32) (param $p i32) (result i32)
      (local $value1 i32)
      (local $value2 i32)
      (local $result i32)

      ;; Получаем значения
      (local.set $value1
        (i32.load
          (i32.add (local.get $this) (global.get $Integer_value_offset))
        )
      )
      (local.set $value2
        (i32.load
          (i32.add (local.get $p) (global.get $Integer_value_offset))
        )
      )

      ;; Проверка деления на ноль
      (if (i32.eq (local.get $value2) (i32.const 0))
        (then
          ;; Деление на ноль - возвращаем 0
          (local.set $result (i32.const 0))
        )
        (else
          ;; Выполняем взятие остатка
          (local.set $result (i32.rem_s (local.get $value1) (local.get $value2)))
        )
      )

      ;; Создаем новый Integer с результатом
      (call $Integer_this_Integer (local.get $result))
    )

    ;; $TODO: Integer_Less_Integer
    (func $Integer_Less_Integer (param $this i32) (param $p i32) (result i32)
      (i32.const 0)
    )

    ;; TODO: $Integer_Less_Real
    (func $Integer_Less_Real (param $this i32) (param $p f32) (result i32)

        (i32.const 0)
    )

    ;; $TODO: Integer_LessEqual_Integer
    (func $Integer_LessEqual_Integer (param $this i32) (param $p i32) (result i32)
      (i32.const 0)
    )

    ;; TODO: $Integer_LessEqual_Real
    (func $Integer_LessEqual_Real (param $this i32) (param $p f32) (result i32)
        (i32.const 0)
    )

    ;; $TODO: Integer_Greater_Integer
    (func $Integer_Greater_Integer (param $this i32) (param $p i32) (result i32)

        (i32.const 0)
    )

    ;; TODO: $Integer_Greater_Real
    (func $Integer_Greater_Real (param $this i32) (param $p f32) (result i32)

        (i32.const 0)
    )

    ;; $TODO: Integer_GreaterEqual_Integer
    (func $Integer_GreaterEqual_Integer (param $this i32) (param $p i32) (result i32)

        (i32.const 0)
    )

    ;; TODO: $Integer_GreaterEqual_Real
    (func $Integer_GreaterEqual_Real (param $this i32) (param $p f32) (result i32)

        (i32.const 0)
    )

    ;; $TODO: Integer_Equal_Integer
    (func $Integer_Equal_Integer (param $this i32) (param $p i32) (result i32)

        (i32.const 0)
    )

    ;; TODO: $Integer_Equal_Real
    (func $Integer_Equal_Real (param $this i32) (param $p f32) (result i32)

        (i32.const 0)
    )


  ;; start merge
  ;; ==================== СТРУКТУРА КЛАССА BOOLEAN ====================
  (global $Boolean_value_offset i32 (i32.const 0))    ;; значение (i32: 0=false, 1=true)
  (global $Boolean_size i32 (i32.const 4))            ;; общий размер: 4 байта

  ;; class Boolean
  ;; TODO $Boolean_ToString_
  (func $Boolean_ToString_ (param $this i32) (result i32) 
  
      (i32.const 0)
  )

  ;; $Boolean_this_Boolean
  (func $Boolean_this_Boolean (param $p i32) (result i32)
    (local $this i32)
    (local $value i32)

    ;; Получаем значение из другого Boolean
    (local.set $value
      (i32.load
        (i32.add (local.get $p) (global.get $Boolean_value_offset))
      )
    )

    ;; Выделяем память
    (local.set $this (call $malloc (global.get $Boolean_size)))

    ;; Инициализируем значение
    (i32.store
      (i32.add (local.get $this) (global.get $Boolean_value_offset))
      (local.get $value)
    )

    (local.get $this)
  )

  ;; $Boolean_this_Integer
  (func $Boolean_this_Integer (param $p i32) (result i32)
    (local $this i32)
    (local $int_value i32)
    (local $bool_value i32)

    ;; Получаем значение Integer
    (local.set $int_value
      (i32.load
        (i32.add (local.get $p) (global.get $Integer_value_offset))
      )
    )

    ;; Конвертируем: 0 -> false(0), не-0 -> true(1)
    (local.set $bool_value
      (i32.ne (local.get $int_value) (i32.const 0))
    )

    ;; Выделяем память
    (local.set $this (call $malloc (global.get $Boolean_size)))

    ;; Инициализируем значение
    (i32.store
      (i32.add (local.get $this) (global.get $Boolean_value_offset))
      (local.get $bool_value)
    )

    (local.get $this)
  )

  ;; $Boolean_toInteger_
  (func $Boolean_toInteger_ (param $this i32) (result i32) 
    (local $bool_value i32)

    ;; Получаем boolean значение
    (local.set $bool_value
      (i32.load
        (i32.add (local.get $this) (global.get $Boolean_value_offset))
      )
    )

    ;; Создаем Integer: true(1) -> 1, false(0) -> 0
    (call $Integer_this_Integer (local.get $bool_value))
  )

  ;; $Boolean_Get_
  (func $Boolean_Get_ (param $this i32) (result i32)
    (i32.load
      (i32.add (local.get $this) (global.get $Boolean_value_offset))
    )
  )

  ;; $Boolean_Or_Boolean
  (func $Boolean_Or_Boolean (param $this i32) (param $p i32) (result i32) 
    (local $value1 i32)
    (local $value2 i32)
    (local $result i32)

    ;; Получаем значения
    (local.set $value1 (call $Boolean_Get_ (local.get $this)))
    (local.set $value2 (call $Boolean_Get_ (local.get $p)))

    ;; Вычисляем ИЛИ: value1 || value2
    (local.set $result
      (i32.or (local.get $value1) (local.get $value2))
    )

    ;; Создаем новый Boolean
    (call $Boolean_this_Integer
      (call $Integer_this_Integer (local.get $result))
    )
  )

  ;; $Boolean_And_Boolean
  (func $Boolean_And_Boolean (param $this i32) (param $p i32) (result i32)
    (local $value1 i32)
    (local $value2 i32)
    (local $result i32)

    ;; Получаем значения
    (local.set $value1 (call $Boolean_Get_ (local.get $this)))
    (local.set $value2 (call $Boolean_Get_ (local.get $p)))

    ;; Вычисляем И: value1 && value2
    (local.set $result
      (i32.and (local.get $value1) (local.get $value2))
    )

    ;; Создаем новый Boolean
    (call $Boolean_this_Integer
      (call $Integer_this_Integer (local.get $result))
    )
  )

  ;; $Boolean_Xor_Boolean
  (func $Boolean_Xor_Boolean (param $this i32) (param $p i32) (result i32) 
    (local $value1 i32)
    (local $value2 i32)
    (local $result i32)

    ;; Получаем значения
    (local.set $value1 (call $Boolean_Get_ (local.get $this)))
    (local.set $value2 (call $Boolean_Get_ (local.get $p)))

    ;; Вычисляем XOR: value1 ^ value2
    (local.set $result
      (i32.xor (local.get $value1) (local.get $value2))
    )

    ;; Создаем новый Boolean
    (call $Boolean_this_Integer
      (call $Integer_this_Integer (local.get $result))
    )
  )

  ;; $Boolean_Not_
  (func $Boolean_Not_ (param $this i32) (result i32) 
    (local $value i32)
    (local $result i32)

    ;; Получаем значение
    (local.set $value (call $Boolean_Get_ (local.get $this)))

    ;; Вычисляем НЕ: !value
    (local.set $result
      (i32.eqz (local.get $value))  ;; 0 если value != 0, 1 если value == 0
    )

    ;; Создаем новый Boolean
    (call $Boolean_this_Integer
      (call $Integer_this_Integer (local.get $result))
    )
  )

  ;; end merge
  ;; $Main_this_
  (func $Main_this_ (export "_start") (result i32)
    (local $a i32)
    (local $b i32)
    (local $result i32)

    ;; Создаем true
    (i32.const 1)
    (call $Integer_this_Integer)
    (call $Boolean_this_Integer)
    (local.set $a)

    ;; Создаем false
    (i32.const 1)
    (call $Integer_this_Integer)
    (call $Boolean_this_Integer)
    (local.set $b)

    ;; Тестируем OR: true OR false = true
    (local.get $a)
    (local.get $b)
    (call $Boolean_Xor_Boolean)
    (local.set $result)

    ;; Возвращаем результат (должно быть 1)
    (call $Boolean_Get_ (local.get $result))
  )
)
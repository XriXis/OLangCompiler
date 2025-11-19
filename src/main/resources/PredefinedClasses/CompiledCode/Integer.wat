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

  ;; start merge
  ;; class Integer

  ;; $Integer_this_Integer
  (func $Integer_this_Integer (param $p i32) (result i32)
    (local.get $p)
  )

  ;; constructor from Real
  ;; $Integer_this_Real
  (func $Integer_this_Real (param $p f32) (result i32)
    ;; Конвертируем Real в Integer (округляем к 0)
    (i32.trunc_f32_s (local.get $p))
  )

  ;; emtpy constructor, set value = 0
  ;; $Integer_this_
  (func $Integer_this_ (result i32)
    (i32.const 0)
  )

  (func $Integer_GetMin_ (result i32)
    (i32.const -2147483648)
  )

  (func $Integer_GetMax_ (result i32)
    (i32.const 2147483647)
  )

  ;; TODO: $Integer_ToString_
  (func $Integer_ToString_ (param $this i32) (result i32)
    (i32.const 0)
  )

  ;; $Integer_Get_
  (func $Integer_Get_ (param $this i32) (result i32)
    (local.get $this)
  )

  ;; $Integer_toReal_
  (func $Integer_toReal_ (param $this i32) (result f32)
    (f32.convert_i32_s (local.get $this))
  )

  ;; $Integer_toBoolean_
  (func $Integer_toBoolean_ (param $this i32) (result i32)
    (i32.ne (local.get $this) (i32.const 0))
  )

  ;; $Integer_UnaryMinus_
  (func $Integer_UnaryMinus_ (param $this i32) (result i32)
    (i32.sub (i32.const 0) (local.get $this))
  )

  ;; $Integer_Plus_Integer
  (func $Integer_Plus_Integer (param $this i32) (param $p i32) (result i32)
    (local.get $this)
    (local.get $p)
    (i32.add)
  )

  ;; $Integer_Plus_real
  (func $Integer_Plus_real (param $this i32) (param $p f32) (result f32)
    (f32.add (f32.convert_i32_s (local.get $this)) (local.get $p))
  )

  ;; $Integer_Minus_Integer
  (func $Integer_Minus_integer (param $this i32) (param $p i32) (result i32)
    (local.get $this)
    (local.get $p)
    (i32.sub)
  )

  ;; $Integer_Minus_Real
  (func $Integer_Minus_Real (param $this i32) (param $p f32) (result f32)
    (f32.sub (f32.convert_i32_s (local.get $this)) (local.get $p))
  )

  ;; $Integer_Mult_Integer
  (func $Integer_Mult_Integer (param $this i32) (param $p i32) (result i32)
    (local.get $this)
    (local.get $p)
    (i32.mul)
  )

  ;; $Integer_Mult_Real
  (func $Integer_Mult_Real (param $this i32) (param $p f32) (result f32)
    (f32.mul (f32.convert_i32_s (local.get $this)) (local.get $p))
  )

  ;; $Integer_Div_Integer
  (func $Integer_Div_Integer (param $this i32) (param $p i32) (result i32)
    (local $value1 i32)
    (local $value2 i32)
    (local $result i32)

    (local.set $value1 (local.get $this))
    (local.set $value2 (local.get $p))
    (local.set $result (i32.div_s (local.get $value1) (local.get $value2)))
    (local.get $result)
  )

  ;; $Integer_Div_Real
  (func $Integer_Div_Real (param $this i32) (param $p f32) (result f32)
    (f32.div (f32.convert_i32_s (local.get $this)) (local.get $p))
  )

  ;; $Integer_Rem_Integer
  (func $Integer_Rem_Integer (param $this i32) (param $p i32) (result i32)
    (local $value1 i32)
    (local $value2 i32)
    (local $result i32)

    (local.set $value1 (local.get $this))
    (local.set $value2 (local.get $p))
    (local.set $result (i32.rem_s (local.get $value1) (local.get $value2)))
    (local.get $result)
  )

  ;; Integer_Less_Integer
  (func $Integer_Less_Integer (param $this i32) (param $p i32) (result i32)
    (i32.lt_s (local.get $this) (local.get $p))
  )

  ;; $Integer_Less_Real
  (func $Integer_Less_real (param $this i32) (param $p f32) (result i32)
    (f32.lt (f32.convert_i32_s (local.get $this)) (local.get $p))
  )

  ;; Integer_LessEqual_Integer
  (func $Integer_LessEqual_Integer (param $this i32) (param $p i32) (result i32)
    (i32.le_s (local.get $this) (local.get $p))
  )

  ;; $Integer_LessEqual_Real
  (func $Integer_LessEqual_Real (param $this i32) (param $p f32) (result i32)
    (f32.le (f32.convert_i32_s (local.get $this)) (local.get $p))
  )

  ;; Integer_Greater_Integer
  (func $Integer_Greater_Integer (param $this i32) (param $p i32) (result i32)
    (i32.gt_s (local.get $this) (local.get $p))
  )

  ;; $Integer_Greater_Real
  (func $Integer_Greater_Real (param $this i32) (param $p f32) (result i32)
    (f32.gt (f32.convert_i32_s (local.get $this)) (local.get $p))
  )

  ;; $Integer_GreaterEqual_Integer
  (func $Integer_GreaterEqual_Integer (param $this i32) (param $p i32) (result i32)
    (i32.ge_s (local.get $this) (local.get $p))
  )

  ;; $Integer_GreaterEqual_Real
  (func $Integer_GreaterEqual_Real (param $this i32) (param $p f32) (result i32)
    (f32.ge (f32.convert_i32_s (local.get $this)) (local.get $p))
  )

  ;; $Integer_Equal_Integer
  (func $Integer_Equal_Integer (param $this i32) (param $p i32) (result i32)
    (i32.eq (local.get $this) (local.get $p))
  )

  ;; $Integer_Equal_Real
  (func $Integer_Equal_Real (param $this i32) (param $p f32) (result i32)
    (f32.eq
      (f32.convert_i32_s
        (local.get $this)
      )
      (local.get $p)
    )
  )

  ;; end merge
  ;; $Main_this_
  (func $Main_this_ (export "_start") (result i32)
    (local $a i32)
    (local $b i32)
    (local $sum i32)
    (local $result_value i32)

    ;; Создаем Integer a = 15
    (i32.const 38)
    (local.set $a)

    ;; Создаем Integer b = 25
    (i32.const 2)
    (local.set $b)

    ;; a = 15, b = 25

    ;; Складываем: a + b
    (local.get $a)
    (local.get $b)
    (call $Integer_LessEqual_Integer)
    (local.set $sum)

    ;; Получаем значение результата
    (local.get $sum)
    (call $Integer_Get_)
    (local.set $result_value)

    ;; Возвращаем результат (должно быть 40)
    (local.get $result_value)
  )
)
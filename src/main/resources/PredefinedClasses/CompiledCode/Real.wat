(module
  ;; ==================== ПАМЯТЬ И СТРУКТУРА ====================
  (memory $0 1)

  (global $top_mem (mut i32) (i32.const 0))
  ;; TODO: $malloc
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
    
    ;; class Real
    (global $Real_Min_Real_offset i32 (i32.const 0))
    
    (global $Real_Max_Real_offset i32 (i32.const 4))
    
    (global $Real_Epsilon_Real_offset i32 (i32.const 8))
    
    ;; TODO: $Real_ToString_
    (func $Real_ToString_ (param $this f32) (result i32)
        (i32.const 0)
    )

    (func $Real_Epsilon_ (result f32)
        (f32.const 0.00000001)
    )

    (func $Real_Min_ (result f32)
        (f32.const -2147483647.0)
    )

    (func $Real_Max_ (result f32)
       (f32.const 2147483648.0)
    )

    (func $Real_this_Real (param $p f32) (result f32) 
        local.get $p
    )

    (func $Real_this_Integer (param $p i32) (result f32) 
        local.get $p
        f32.convert_i32_s
    )

    (func $Real_this_ (result f32) 
        (f32.const 0.0)
    )
    
    (func $Real_toInteger_ (param $this f32) (result i32)
        local.get $this
        i32.trunc_f32_s
    )

    (func $Real_UnaryMinus_ (param $this f32) (result f32)
        local.get $this
        f32.neg
    )

    (func $Real_Plus_Real (param $this f32) (param $p f32) (result f32)
        local.get $this
        local.get $p
        f32.add
    )

    (func $Real_Plus_Integer (param $this f32) (param $p i32) (result f32) 
        local.get $p
        f32.convert_i32_s
        local.get $this
        f32.add
    )

    (func $Real_Minus_Real (param $this f32) (param $p f32) (result f32) 
        local.get $this
        local.get $p
        f32.sub
    )

    (func $Real_Minus_Integer (param $this f32) (param $p i32) (result f32) 
        local.get $this
        local.get $p
        f32.convert_i32_s
        f32.sub
    )
    
    (func $Real_Mult_Real (param $this f32) (param $p f32) (result f32)
        local.get $this
        local.get $p
        f32.mul
    )

    (func $Real_Mult_Integer (param $this f32) (param $p i32) (result f32) 
        local.get $this
        local.get $p
        f32.convert_i32_s
        f32.mul
    )

    (func $Real_Div_Integer (param $this f32) (param $p i32) (result f32) 
        local.get $this
        local.get $p
        f32.convert_i32_s
        f32.div
    )

    (func $Real_Div_Real (param $this f32) (param $p f32) (result f32)
        local.get $this
        local.get $p
        f32.div
    )

    (func $Real_Rem_Integer (param $this f32) (param $p i32) (result f32)
        local.get $this
        local.get $p
        f32.convert_i32_s
        f32.div
        local.set $this
        local.get $this
        i32.trunc_f32_s
        f32.convert_i32_s
        local.get $this
        f32.sub
    )

    (func $Real_Less_Real (param $this f32) (param $p f32) (result i32) 
        local.get $this
        local.get $p
        f32.sub
        call $Real_Epsilon_
        f32.neg
        f32.lt
    )

    (func $Real_Less_Integer (param $this f32) (param $p i32) (result i32)
        local.get $this
        local.get $p
        f32.convert_i32_s
        f32.sub
        call $Real_Epsilon_
        f32.neg
        f32.lt
    )

    (func $Real_LessEqual_Real (param $this f32) (param $p f32) (result i32) 
        local.get $this
        local.get $p
        f32.sub
        call $Real_Epsilon_
        f32.lt
    )

    (func $Real_LessEqual_Integer (param $this f32) (param $p i32) (result i32) 
        local.get $this
        local.get $p
        f32.convert_i32_s
        f32.sub
        call $Real_Epsilon_
        f32.lt
    )

    (func $Real_Greater_Real (param $this f32) (param $p f32) (result i32)
        local.get $this
        local.get $p
        call $Real_LessEqual_Real
        i32.eqz
    )
    
    (func $Real_Greater_Integer (param $this f32) (param $p i32) (result i32)
        local.get $this
        local.get $p
        call $Real_LessEqual_Integer
        i32.eqz
    )

    (func $Real_GreaterEqual_Real (param $this f32) (param $p f32) (result i32)
        local.get $this
        local.get $p
        call $Real_Less_Real
        i32.eqz
    )

    (func $Real_GreaterEqual_Integer (param $this f32) (param $p i32) (result i32)
        local.get $this
        local.get $p
        call $Real_Less_Integer
        i32.eqz
    )

    (func $Real_Equal_Real (param $this f32) (param $p f32) (result i32)
        local.get $this
        local.get $p
        f32.sub
        f32.abs
        call $Real_Epsilon_
        f32.gt
    )

    (func $Real_Equal_Integer (param $this f32) (param $p i32) (result i32)
        local.get $this
        local.get $p
        f32.convert_i32_s
        call $Real_Less_Real
    )
  ;; end merge
    (func $Main_this_ (export "_start") (result i32)
      (local $a f32)
      (local $b f32)
      (local $c f32)
      (local $result f32)
      (local $comparison i32)

      ;; Создаем Real a = 15.5
      (f32.const 15.5)
      (local.set $a)

      ;; Создаем Real b = 7.2
      (f32.const 7.2)
      (local.set $b)

      ;; Тестируем сложение: a + b
      (local.get $a)
      (local.get $b)
      (call $Real_Plus_Real)
      (local.set $c)

      ;; Тестируем вычитание: c - 3.0
      (local.get $c)
      (f32.const 3.0)
      (call $Real_Minus_Real)
      (local.set $result)

      ;; Тестируем сравнение: result > 20.0
      (local.get $result)
      (f32.const 20.0)
      (call $Real_Greater_Real)
      (local.set $comparison)

      ;; Возвращаем результат сравнения (1 если true, 0 если false)
      ;; Ожидаем: 22.7 - 3.0 = 19.7 > 20.0? false (0)
      (local.get $comparison)
    )
 )
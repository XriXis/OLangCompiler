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
        call $Real_LessEqual_Real
        i32.eqz
    )
    
    (func $Real_Greater_Integer (param $this f32) (param $p i32) (result i32)
        call $Real_LessEqual_Integer
        i32.eqz
    )

    (func $Real_GreaterEqual_Real (param $this f32) (param $p f32) (result i32) 
        call $Real_Less_Real
        i32.eqz
    )

    (func $Real_GreaterEqual_Integer (param $this f32) (param $p i32) (result i32) 
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
 )
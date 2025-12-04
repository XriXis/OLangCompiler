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

      (func $Array_this_ (result i32)
        i32.const 0
        call $Array_this_Integer
      )

      (func $copy_help (param $from i32) (param $to i32) (param $count i32)
          (local $i i32) ;; индекс/смещение в байтах
          (local.set $i (i32.const 0))

          block $exit
              loop $loop
                  ;; если i >= count*4 (байтов) — выходим
                  (br_if $exit (i32.ge_s (local.get $i) (i32.mul (local.get $count) (i32.const 4))))

                  ;; копируем элемент: to[i] = from[i]
                  (i32.store
                      (i32.add (local.get $to) (local.get $i))
                      (i32.load (i32.add (local.get $from) (local.get $i)))
                  )

                  ;; переходим к следующему элементу (4 байта)
                  (local.set $i (i32.add (local.get $i) (i32.const 4)))
                  br $loop
              end
          end
      )

      (func $Array_this_Array_Integer (param $copy i32) (param $newLen i32) (result i32)
        (local $this i32) (local $copy_count i32)
            local.get $newLen
            call $Array_this_Integer
            local.set $this
            ;; actual copy len
            (local.set $copy_count
                (if (result i32) (i32.lt_s (local.get $newLen) (i32.load (local.get $copy)))
                    (then (local.get $newLen))
                    (else (i32.load (local.get $copy)))
                )
            )
            ;; copy
            (call $copy_help
                (i32.add (i32.const 4) (local.get $copy))   ;; from
                (i32.add (i32.const 4) (local.get $this))   ;; to
                (local.get $copy_count)) ;; count
            local.get $this
      )

      (func $Array_this_Integer (param $l i32) (result i32)
        (local $this i32)
        i32.const 1
        local.get $l
        i32.add
        i32.const 4
        i32.mul
        call $malloc
        local.set $this

        local.get $this
        local.get $l
        i32.store

        local.get $this
      )

      (func $Array_Length_ (param $this i32) (result i32)
        local.get $this
        i32.load
      )

      (func $Array_get_Integer (param $this i32) (param $i i32) (result i32)
        ;; todo check on length and do ??corresponding?? action
        local.get $i
        i32.const 4
        i32.mul
        ;; adjust 0-based indexing
        i32.const 4
        i32.add

        local.get $this
        i32.add
        i32.load
      )

      (func $Array_set_Integer_T (param $this i32) (param $i i32) (param $v i32)
        local.get $i
        i32.const 4
        i32.mul
        i32.const 4
        i32.add
        local.get $this
        i32.add
        local.get $v
        i32.store
      )

  ;; start merge
  ;; ==================== class String ====================
    (func $String_this_ (result i32)
      (call $Array_this_)
    )

    (func $String_this_Array (param $self i32) (result i32) (local $this i32)
       (call $Array_this_Array_Integer (local.get $self) (i32.load (local.get $self)))
    )


    (func $String_add_String (param $this i32) (param $another i32) (result i32)
        (local $res i32)
        (local.set $res
            (call $Array_this_Array_Integer
                (local.get $this)
                (i32.add
                    (i32.load (local.get $this))
                    (i32.load (local.get $another))
                )
            )
        )
        (call $copy_help
            (i32.add (i32.const 4)(local.get $another))
            (i32.add
                ;; skip len
                (i32.add
                    (local.get $res)
                    (i32.const 4))
                ;; skip already copied
                (i32.mul
                    (i32.const 4)
                    (i32.load (local.get $this))
                )
            )
            (i32.load (local.get $another))
        )
        (local.get $res)
    )
  ;; end merge

  (func $Main_this_ (export "_start") (result i32)
      (local $s1 i32)
      (local $s2 i32)
      (local $s3 i32)
      (local $tmp i32)

      ;; === Test 1: Constructor ===

      ;; Создали строку 1
      (local.set $s1
          (call $String_this_)
      )

      ;; Проверяем созданную строку (длина должна быть 0)
      (if (i32.ne (i32.load (local.get $s1)) (i32.const 0))
          (return (i32.const 101)) ;; error code 101
      )

      ;; === Test 2: Создаем String из массива (len=3) ===
      ;; Считаем, что в памяти мы заранее положили массив
      ;; но сейчас симулируем созданием Array на len=3

      (local.set $s2
          (call $Array_this_Array_Integer (i32.const 0) (i32.const 3))
      )

      (local.set $s2
          (call $String_this_Array (local.get $s2))
      )

      ;; Проверяем длину
      (if (i32.ne (i32.load (local.get $s2)) (i32.const 3))
          (return (i32.const 102))
      )

      ;; === Test 3: String_add_String ===
      ;; $s1 length=0
      ;; $s2 length=3
      ;; ожидаем length=3

      (local.set $s3
          (call $String_add_String (local.get $s1) (local.get $s2))
      )

      (if (i32.ne (i32.load (local.get $s3)) (i32.const 3))
          (return (i32.const 103))
      )

      ;; === Test 4: add non-empty + non-empty
      ;; создадим строку длины 2

      (local.set $tmp
          (call $Array_this_Array_Integer (i32.const 0) (i32.const 2))
      )
      (local.set $tmp
          (call $String_this_Array (local.get $tmp))
      )

      ;; ожидаем length = 3 + 2 = 5
      (local.set $s3
          (call $String_add_String (local.get $s2) (local.get $tmp))
      )


      (if (i32.ne (i32.load (local.get $s3)) (i32.const 5))
          (return (i32.const 104))
      )

      ;; если дошли сюда — тесты пройдены

      (return (i32.const 0))
  )
)

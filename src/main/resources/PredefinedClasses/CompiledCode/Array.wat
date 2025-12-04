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

  ;; class Array
  ;; todo: ToString_ (param $this i32) (result i32) 
  (func $Array_ToString_ (param $this i32) (result i32)
    (i32.const 0)
  )

  ;; todo: toList_ (param $this i32) (result i32)
  (func $Array_toList_ (param $this i32) (result i32)
    (i32.const 0)
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

  ;; end merge
  ;; $Main_this_
    (func $Main_this_ (export "_start") (result i32)
      (local $arr1 i32)
      (local $arr2 i32)
      (local $len i32)
      (local $elem i32)
      (local $test_result i32)

      ;; Создаем массив [10, 20, 30] длиной 3
      (i32.const 3)
      (call $Array_this_Integer)
      (local.set $arr1)

      ;; Заполняем массив значениями
      (local.get $arr1)
      (i32.const 0)
      (i32.const 10)
      (call $Array_set_Integer_T)

      (local.get $arr1)
      (i32.const 1)
      (i32.const 20)
      (call $Array_set_Integer_T)

      (local.get $arr1)
      (i32.const 2)
      (i32.const 30)
      (call $Array_set_Integer_T)

      ;; Тест 1: Получаем длину массива
      (local.get $arr1)
      (call $Array_Length_)
      (local.set $len)

      ;; Тест 2: Получаем элемент по индексу 1 (должен быть 20)
      (local.get $arr1)
      (i32.const 1)
      (call $Array_get_Integer)
      (local.set $elem)

      ;; Создаем второй массив как копию первого с новой длиной
      (local.get $arr1)
      (i32.const 2)  ;; новая длина
      (call $Array_this_Array_Integer)
      (local.set $arr2)

      ;; Тест 3: Проверяем длину второго массива (должна быть 2)
      (local.get $arr2)
      (call $Array_Length_)
      (local.set $len)

      ;; Тест 4: Проверяем, что элементы скопировались
      (local.get $arr2)
      (local.set $elem (call $Array_get_Integer (i32.const 0)))

      ;; Возвращаем результат последнего теста (элемент arr2[0], должен быть 10)
      (local.get $elem)
    )
)
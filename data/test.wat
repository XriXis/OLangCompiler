(module
  (type $fnIntThis (func (param i32) (result i32)))

  ;; Таблица виртуальных методов для MyClass
  (table $vtable 1 funcref)

  ;; Заполняем vtable
  (elem (i32.const 0) $MyClass_add_impl)

  (memory (export "memory") 1)

  ;; Константы: поля объекта MyClass
  (global $offset_vtable (export "OFF_VTABLE") i32 (i32.const 0))
  (global $offset_a      (export "OFF_A")      i32 (i32.const 4))
  (global $offset_b      (export "OFF_B")      i32 (i32.const 8))

  ;; Total object size = 12 bytes
  (global $size_MyClass (export "SIZE_MyClass") i32 (i32.const 12))

  ;; -------- Object constructor --------
  (func $MyClass_new (export "MyClass_new") (result i32)
    ;; Allocate on heap
    (local $ptr i32)
    local.set $ptr (i32.const 42) ;; Some memotry trace logic

    ;; Write vtable pointer
    local.get $ptr
    i32.const 0       ;; index of vtable = 0
    i32.store

    ;; Write field a = 10
    local.get $ptr
    global.get $offset_a
    i32.add
    i32.const 10
    i32.store

    ;; Write field b = 20
    local.get $ptr
    global.get $offset_b
    i32.add
    i32.const 20
    i32.store

    ;; return pointer to object
    local.get $ptr
  )


  ;; ------------ Virtual function implementation ------------
  ;; MyClass.add(): Integer = a + b
  (func $MyClass_add_impl (type $fnIntThis) (param $this i32) (result i32)
    ;; load a
    local.get $this
    global.get $offset_a
    i32.add
    i32.load

    ;; load b
    local.get $this
    global.get $offset_b
    i32.add
    i32.load

    i32.add
  )


  ;; ------------ Dynamic dispatch ------------
  ;; Call virtual method #0 on object
  (func $MyClass_add (export "MyClass_add") (param $this i32) (result i32)
    ;; Load vtable index
    local.get $this
    global.get $offset_vtable
    i32.add
    i32.load        ;; ← this is the index into table

    local.get $this ;; implicit arg

    call_indirect (type $fnIntThis)
  )

  ;; Simple bump allocator pointer
  (global $heapPtr (mut i32) (i32.const 1024))
)

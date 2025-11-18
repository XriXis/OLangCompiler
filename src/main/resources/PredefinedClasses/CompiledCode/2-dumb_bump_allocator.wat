;; This module is just a simpliest stub, that immitates the implemented heap logic.
;; But in case of this module usage - memory leak is GUARANTEED!!!
;; Look at leading comment in memory_mng_module.wat for more details

;; (memory $0 1) => (memory (export "memory") 1)
(module
  (memory $0 1)
  ;; start merge
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
    ;; end merge
)
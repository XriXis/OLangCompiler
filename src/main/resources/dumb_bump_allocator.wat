;; This module is just a simpliest stub, that immitates the implemented heap logic.
;; But in case of this module usage - memory leak is GUARANTEED!!!
;; Look at leading comment in memory_mng_module.wat for more details
(module
    (memory $0 1)
    (global $top_mem i64)
    (func $malloc (param $alloc_size i32) (result i64)
        (local $res i64)
        local.set $res (global.get $top_mem)
        global.set
            $top_mem
            (i64.add
                (i64.extend_s/i32 (local.get $alloc_size))
                (global.get $top_mem)
            )
        local.get $res
    )
)
;; This module is not complete and planned to be done in case of reamining time before project defence.
;; Most adequate thought - write own malloc and GC on C, compile it to WASM, and link to compiled O-Lang module
(module
    (memory 1)
    (global $heap_ini i64 (i32.const 0))
    (global $heap_ptr i64 (i64.const 1))
    (global $heap_cap i64 (i64.const 64))
    (func $malloc (param i32) (result i64)

    )

    ;; add tuple to very end of the heap (as to list)
    (func $add_to_heap_end (param $t0 i64) (param $t1 i64)
        (i64.store
            (global.get $heap_ptr)
            (local.get $t0)
        )
        (i64.store
            (i64.add
            ;;            -v- ptr size (size of i64 in bytes)
                (i64.const 8)
                (global.get $heap_ptr)
            )
            (local.get $t1)
        )
        (global.set
            $heap_ptr
            (i64.add
                (global.get $heap_ptr)
                (i64.const 16)
            )
        )
        i64.sub (global.get $heap_ptr) (global.get $heap_ini)
        i64.gt_u
        if
            call $heap_reallocate
        end
    )

    (func $copy (param $copy_from_addr i64) (param $copy_to_addr i64) (param $copy_len i64)
        (local $i i64)
        (local $copy_offset i64)
        (local $i_cap i64)
        local.set $i           (i64.sub (local.get $copy_from_addr) (i64.const 8))
        local.set $copy_offset (i64.sub (local.get $copy_to_addr) (local.get $copy_from_addr))
        local.set $i_cap       (i64.add (local.get $i) (local.get $copy_len))
        block $copy_loop_br
            loop $copy_loop
                local.set $i (i64.add (local.get $i) (i64.const 8))
                br_if $copy_loop_br (i64.ge_u (local.get $i) (local.get $i_cap))
                i64.store
                    (i64.add
                        (local.get $copy_offset)
                        (local.get $i))
                    (i64.load (local.get $i))
                br $copy_loop
            end
        end
    )

    ;; list growth process
    (func $heap_reallocate
        local.set $old_heap_ini (global.get $heap_ini)
        local.set $old_heap_ptr (global.get $heap_ptr)

        local.set $old_size
            (i64.sub
                (local.get $old_heap_ptr)
                (local.get $old_heap_ini)
            )

        global.set $heap_ini (i64.load (global.get $heap_ini))
        global.set
            $heap_ptr
            (i64.add
                (global.get $heap_ini)
                (local.get $old_size)
            )
        global.set
            $heap_cap
            (i64.add
                (local.get $old_size)
                (global.get $heap_ptr)
            )

        call $copy (local.get $old_heap_ini) (global.get $heap_ini) (local.get $old_size)
    )

    (func $init_heap
        ;; put oh heap start and end of free block (after the binary heap, untill end of whole memory)
        call $add_heap_end (global.get $heap_cap) (i64.mul (memory.size) (i64.const 65536))
        ;;                                                        page size (64kb) -^^^^^-
    )
    (start $init_heap)
)
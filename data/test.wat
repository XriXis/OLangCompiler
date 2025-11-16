(module
  ;; Import console_log for printing to the console (provided by the runtime)
  (import "env" "console_log" (func $console_log (param i32)))

  ;; MyClass constructor-like function (creates an object with 'a' and 'b' values)
  (func $MyClass_new (result i32)
    ;; Declare 'a' as a local variable
    (local $a i32)
    ;; Declare 'b' as a local variable
    (local $b i32)

    ;; Initialize 'a' to 10
    (i32.const 10)  ;; a = 10
    (local.set $a)


    ;; Initialize 'b' to 20
    (i32.const 20)  ;; b = 20
    (local.set $b)

    ;; Return a pointer to the object (in this case just 0 as a placeholder)
    (i32.const 0)  ;; Object reference (simplified)
  )

  ;; Method 'add' for MyClass (add the values of 'a' and 'b')
  (func $add (param $obj i32) (result i32)
    ;; Retrieve 'a' and 'b' values
    (local.get $a)  ;; Get value of 'a'
    (local.get $b)  ;; Get value of 'b'

    ;; Perform addition (a + b)
    (i32.add (local.get $a) (local.get $b))
  )

  ;; Main function: create MyClass instance, call 'add', and print the result
  (func $main
    ;; Create MyClass object (equivalent to obj := MyClass())
    (call $MyClass_new)

    ;; Call the 'add' method (equivalent to result := obj.add())
    (call $add)

    ;; Print the result (equivalent to Console().print(result.Get().ToString()))
    (call $console_log)
  )

  ;; Export functions for external use (starting point is '_start' by default)
  (export "main" (func $main))
  (export "_start" (func $main))  ;; _start is the entry point for WebAssembly
)

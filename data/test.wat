(module
  ;; Define the 'add' function that takes two i32 parameters and returns an i32 result.
  (func $add (param $a i32) (param $b i32) (result i32)
    ;; Add the two parameters
    (i32.add (local.get $a) (local.get $b))
  )

  ;; Define the '_start' function that will be invoked at the start
  (func $_start (result i32)
    ;; Call the 'add' function with two arguments
    (call $add (i32.const 5) (i32.const 10))
  )

  ;; Export the 'add' function to be accessible from outside the module
  (export "add" (func $add))

  ;; Export the '_start' function to be invoked when the module starts
  (export "_start" (func $_start))
)

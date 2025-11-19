(module
  ;; 1. Объявляем тип функции
  (type $Formatter (func (param i32) (result i32)))

  ;; 2. Создаем разные реализации
  (func $formatDecimal (type $Formatter) (param $num i32) (result i32)
    ;; Просто возвращаем число как есть
    (local.get $num)
  )

  (func $formatHex (type $Formatter) (param $num i32) (result i32)
    ;; В реальности здесь была бы конвертация в hex
    (i32.const 0x100) ;; адрес hex строки
  )

  ;; 3. Полиморфная функция
  (func $formatNumber (param $num i32) (param $formatter i32) (result i32)
    (call_indirect (type $Formatter)
      (local.get $num)
      (local.get $formatter)
    )
  )

  ;; 4. Таблица функций
  (table 2 funcref)
  (elem (i32.const 0) $formatDecimal $formatHex)

  ;; 5. Использование
  (func $test (export "_start") (result i32)
    ;; Один вызов - разное поведение!
;;    (call $formatNumber (i32.const 42) (i32.const 0)) ;; decimal
    (call $formatNumber (i32.const 42) (i32.const 1)) ;; hex
  )
)
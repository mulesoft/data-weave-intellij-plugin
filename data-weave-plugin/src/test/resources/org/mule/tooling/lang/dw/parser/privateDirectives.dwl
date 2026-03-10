%dw 2.0
output application/json

private fun a() = "a"

private fun hi(name: String): String = "Hi " ++ name
---
{
  key: hi("Mariano")
}
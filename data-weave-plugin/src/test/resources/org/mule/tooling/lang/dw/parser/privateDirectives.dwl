%dw 2.0
output application/json

private fun hi(name: String): String = "Hi " ++ name
---
{
  hi: hi("Mariano")
}
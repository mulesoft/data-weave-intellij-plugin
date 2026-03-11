%dw 2.0
output application/json

private ns ns0 http://dataweave.com

private type User = {
  name: String,
  lastName: String
}

private var HI = "Hi"

private fun hi(user: User): String = do {
  private var SEPARATOR = " "
  ---
  HI ++ SEPARATOR ++ user.lastName ++ SEPARATOR ++ user.name
}

---
{
  hi: HI,
  user: hi({
    name: "John",
    lastName: "Doe"
  }),
  ns0#number: 123
}
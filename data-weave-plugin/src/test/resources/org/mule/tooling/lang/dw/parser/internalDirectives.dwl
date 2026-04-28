%dw 2.0
output application/json

internal ns ns0 http://dataweave.com

internal type User = {
  name: String,
  lastName: String
}

internal var HI = "Hi"

internal fun hi(user: User): String = do {
  internal var SEPARATOR = " "
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
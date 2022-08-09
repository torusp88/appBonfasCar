package danilopereira.bonfascar.entities

import com.google.gson.annotations.SerializedName

class Wash (
    @SerializedName("id")
    var id: Long = 0,

    @SerializedName("date")
    var date: Int = 0,

    @SerializedName("car")
    var car: Car = Car("", "","", 0,""),

    @SerializedName("deliver")
    var deliver: Boolean = false,

    @SerializedName("price")
    var price: Int = 0,

    @SerializedName("wax")
    var wax: Boolean = false,

    @SerializedName("payed")
    var payed: Boolean = false,

    @SerializedName("obs")
    var obs: String = ""
    )

/*
"id": 3,
        "date": "2",
        "car": {
            "plate": "AAA1J36",
            "name": "TCross",
            "owner": "Thiago",
            "telephone": 985345612,
            "address": "Rua Judite França Costa 360"
        },
        "deliver": false,
        "price": 50,
        "wax": false,
        "payed": true,
        "obs": "Aspirar bem o porta malas"
 */
package danilopereira.bonfascar.entities

import com.google.gson.annotations.SerializedName

class Car (

    @SerializedName("plate")
    var plate: String = "",

    @SerializedName("name")
    var name: String = "",

    @SerializedName("owner")
    var owner: String = "",

    @SerializedName("telephone")
    var telephone: Int = 0,

    @SerializedName("address")
    var address: String = ""

    )

/*
        "plate": "AAA1J36",
        "name": "TCross",
        "owner": "Thiago",
        "telephone": 985345612,
        "address": "Rua Judite França Costa 360"
 */
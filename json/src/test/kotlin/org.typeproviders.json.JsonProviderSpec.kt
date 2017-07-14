import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.specs.StringSpec
import org.typeproviders.json.JsonProvider

class JsonProviderSpec : StringSpec() {
    init {
        "mapSourceToTree should map a simple object one layer" {
            val sut = JsonProvider()
            val json ="""
                {
                    "field1": true,
                    "field2": null,
                    "field3": 1,
                    "field4": "string"
                }"""

            val ttype = sut.mapSourceToTree(json)

            ttype shouldNotBe (null)
        }
    }
}

//***********************************************************************************************
// Servidor MongoDB en memoria (abrirlo al iniciar el programa y cerrarlo al finalizar)
//***********************************************************************************************

import java.io.File
import org.bson.Document
import org.json.JSONArray
import org.bson.json.JsonWriterSettings

import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.memory.MemoryBackend

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters


// ****************
// **** BD ****
// ****************

//variables globales definidas sin inicializar

lateinit var servidor: MongoServer
lateinit var cliente: MongoClient
lateinit var uri: String
lateinit var db: MongoDatabase
lateinit var coleccionPlantas: MongoCollection<Document>

//ruta al fichero original
const val rutaPlantas ="src/main/resources/florabotanica_plantas.json"

//ruta al fichero de exportación (para mantener copia del original)
const val rutaPlantas2 ="src/main/resources/florabotanica_plantas2.json"

const val nomBaseDatos ="florabotanica"
const val nomColeccionPlantas ="plantas"


// Función para conectar a la BD
fun conectarBD() {
    servidor = MongoServer(MemoryBackend())
    val address = servidor.bind()
    uri = "mongodb://${address.hostName}:${address.port}"

    //abrir conexión con el servidor
    cliente = MongoClients.create(uri)

    //Base de datos
    db = cliente.getDatabase(nomBaseDatos)

    //colecciones
    coleccionPlantas = db.getCollection(nomColeccionPlantas)

    println("Servidor MongoDB en memoria iniciado en $uri")



}

// Función para desconectar a la BD
fun desconectarBD() {
    cliente.close()
    servidor.shutdown()
    println("Servidor MongoDB en memoria finalizado")
}


// ****************************
// **** programa principal ****
// ****************************

fun main() {
    println("exam 3 AD base")

    conectarBD()
    menu()
    desconectarBD()
}



// ****************
// **** MENÚ   ****
// ****************

fun menu(){
    while (true) {

        println("\n****************************")
        println("1) Importar datos desde json")
        println("2) Exportar datos a json")
        println("3) Listar")
        println("4) Insertar")



        println("0) Salir")
        print("--> Selecciona una opción: ")

        // when (scanner.nextLine().trim()) {

        when (readLine()?.trim()) {
            "1" -> importarDatos()
            "2" -> exportarDatos()
            "3" -> listar()
            "4" -> insertarPlanta()

            "0" -> {
                println("Saliendo del programa ...")
                break
            }
            else -> println("Opción no válida. Inténtalo de nuevo.")
        }
    }
}


fun importarDatos(){
    importarBD(rutaPlantas, coleccionPlantas)
}

fun exportarDatos(){
    exportarBD(coleccionPlantas,rutaPlantas2)
}


fun listar() {
    println("\n**** Listado:")
    coleccionPlantas.find().forEach { doc ->
        println(
            "[${doc.getInteger("id_planta")}] " +
                    "nombre_comun: ${doc.getString("nombre_comun")} " +
                    "altura: ${doc.getInteger("altura")} " +
                    "nombre_cientifico: ${doc.getString("nombre_cientifico")} "
        )
    }
}

fun insertarPlanta() {
    //conectar con la BD

    val coleccionPlantas = coleccionPlantas

    print("ID de la planta: ")
    val id_planta = checkID(coleccionPlantas,"id_planta")
    print("nombre_comun: ")
    val nombre_comun = isString()
    print("altura: ")
    val altura = isInt()
    print("nombre_cientifico: ")
    val nombre_cientifico = isString()


    val doc = Document("id_planta", id_planta)
        .append("nombre_comun", nombre_comun)
        .append("altura", altura)
        .append("nombre_cientifico", nombre_cientifico)


    coleccionPlantas.insertOne(doc)
    println("Planta insertado con ID: ${doc.getObjectId("_id")}")
}
fun actualizarPlanta() {
    val coleccionPlantas = coleccionPlantas

    print("ID de la planta a modificar: ")
    val id_planta = isInt()

    val planta = coleccionPlantas
        .find(Filters.eq("id_planta", id_planta))
        .firstOrNull()

    if (planta == null) {
        println("No se encontró ninguna planta con id_planta = \"$id_planta\".")
    } else {
        println(
            "Planta encontrada (" +
                    "nombre_comun: ${planta.getString("nombre_comun")} " +
                    "altura: ${planta.getInteger("altura")} " +
                    "nombre_cientifico: ${planta.getString("nombre_cientifico")})"
        )

        print("Nuevo nombre común: ")
        val nombre_comun = isString()
        print("Nueva altura: ")
        val altura = isInt()
        print("Nuevo nombre científico: ")
        val nombre_cientifico = isString()

        val result = coleccionPlantas.updateOne(
            Filters.eq("id_planta", id_planta),
            Document(
                "\$set", Document()
                    .append("nombre_comun", nombre_comun)
                    .append("altura", altura)
                    .append("nombre_cientifico", nombre_cientifico)
            )
        )

        if (result.modifiedCount > 0)
            println("Planta actualizada correctamente.")
        else
            println("No se modificó ningún documento.")
    }
}

fun eliminarPlanta() {
    val coleccionPlantas = coleccionPlantas

    print("ID de la planta a eliminar: ")
    val id_planta = isInt()

    val result = coleccionPlantas.deleteOne(Filters.eq("id_planta", id_planta))

    if (result.deletedCount > 0)
        println("Planta eliminada correctamente.")
    else
        println("No se encontró ninguna planta con ese ID.")
}




fun isInt():Int{
    while (true){
        val entrada= readln().toIntOrNull()
        if (entrada==null){
            println("Dame un número valido")
        }else{
            return entrada
        }
    }
}
fun isDouble(): Double{
    while (true){
        val entrada= readln().toDoubleOrNull()
        if (entrada==null){
            println("Dame un número valido(Double)")
        }else{
            return entrada
        }
    }
}
fun isString(): String{
    while (true){
        val entrada= readln()
        if (entrada.isBlank()){
            println("Dame un string valido")
        }else{
            return entrada
        }
    }
}


fun checkID(coleccion: MongoCollection<Document>, campo: String):Int{

    while (true){
        val entrada= readln().toIntOrNull()
        if (entrada==null){
            println("Dame un número valido")
        }else{
            var encontrado=false
            coleccion.find().forEach { doc ->
                if (doc.getInteger(campo)==entrada){
                    encontrado=true
                }
            }
            if (!encontrado){
                return entrada
            }else{
                println("Dame un ID valido, ID $entrada ya existe")
            }
        }
    }
}

// *****************************
// **** importar  / exportar****
// *****************************
fun importarBD(rutaJSON: String, coleccion: MongoCollection<Document>) {
    println("Iniciando importación de datos desde JSON...")

    val jsonFile = File(rutaJSON)
    if (!jsonFile.exists()) {
        println("No se encontró el archivo JSON a importar")
        return
    }

    // Leer JSON del archivo
    val jsonText = try {
        jsonFile.readText()
    } catch (e: Exception) {
        println("Error leyendo el archivo JSON: ${e.message}")
        return
    }

    val array = try {
        JSONArray(jsonText)
    } catch (e: Exception) {
        println("Error al parsear JSON: ${e.message}")
        return
    }

    // Convertir JSON a Document y eliminar _id si existe
    val documentos = mutableListOf<Document>()
    for (i in 0 until array.length()) {
        val doc = Document.parse(array.getJSONObject(i).toString())
        doc.remove("_id")  // <-- eliminar _id para que Mongo genere uno nuevo
        documentos.add(doc)
    }

    if (documentos.isEmpty()) {
        println("El archivo JSON está vacío")
        return
    }

    val nombreColeccion =coleccion.namespace.collectionName

    // Borrar colección si existe
    if (db.listCollectionNames().contains(nombreColeccion)) {
        db.getCollection(nombreColeccion).drop()
        println("Colección '$nombreColeccion' eliminada antes de importar.")
    }

    // Insertar documentos
    try {
        coleccion.insertMany(documentos)
        println("Importación completada: ${documentos.size} documentos de $nombreColeccion.")
    } catch (e: Exception) {
        println("Error importando documentos: ${e.message}")
    }
}

fun exportarBD(coleccion: MongoCollection<Document>, rutaJSON: String) {
    val settings = JsonWriterSettings.builder().indent(true).build()
    val file = File(rutaJSON)
    file.printWriter().use { out ->
        out.println("[")
        val cursor = coleccion.find().iterator()
        var first = true
        while (cursor.hasNext()) {
            if (!first) out.println(",")
            val doc = cursor.next()
            out.print(doc.toJson(settings))
            first = false
        }
        out.println("]")
        cursor.close()
    }

    println("Exportación de ${coleccion.namespace.collectionName} completada")
}

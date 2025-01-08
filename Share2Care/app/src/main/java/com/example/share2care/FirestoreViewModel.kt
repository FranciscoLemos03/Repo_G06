package com.example.share2care

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class FirestoreViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    data class LojaSocialData(
        val nome: String,
        val descricao: String,
        val imagemUrl: String? = null
    )

    data class AnuncioData(
        val id: String,
        val titulo: String,
        val motivo: String,
        val meta: String,
        val necessidades: String,
        val descricao: String,
        val link: String,
        val requisitos: String,
        val lojaSocial: String,
        val data_criacao: Date,
        val tipo: String,
        val imagemUrl: String?
    )

    data class AllAnuncios(
        val id: String = "",
        val tipo: String = "",
        val titulo: String = "",
        val imagemUrl: String? = null,
        val dataCriacao: Date = Date(),
        val motivo: String = "",
        val meta: String = "0",
        val necessidades: String = "",
        val descricao: String = "",
        val link: String = "",
        val requisitos: String = "",
        val lojaSocialName: String = "",
        val imageUrlLojaSocial: String? = null
    )

    data class Tickets(
        val id: String = "",
        val anuncio_id: String = "",
        val anuncio_titulo: String = "",
        val motivo: String = "",
        val email: String = "",
        val creation_date: Date = Date(),
        val nome: String = "",
        val tipo: String = "",
        val condicao: String = "",
        val descricao: String = "",
        val imagemUrl: String = "",
        val listaBens: String = "",
        val quantidade: String = "",
        val status: String = ""
    )

    data class AgregadosData(
        val id: String = "",
        val num_doc: String = "",
        val data_criacao: Date = Date(),
        val loja_id: String = ""
    )

    data class BeneficiarioData(
        val id: String,
        val agregado_id: String,
        val nome: String,
        val telemovel: String,
        val nacionalidade: String,
        val limite: Boolean
    )

    private val _lojaSocialData = MutableLiveData<LojaSocialData>()
    val lojaSocialData: LiveData<LojaSocialData> = _lojaSocialData

    private val _anuncioData = MutableLiveData<List<AnuncioData>>()
    val anuncioData: LiveData<List<AnuncioData>> = _anuncioData

    private val _allAnuncios = MutableLiveData<List<AllAnuncios>>()
    val allAnuncios: LiveData<List<AllAnuncios>> get() = _allAnuncios

    private val _agregadoData = MutableLiveData<List<AgregadosData>>()
    val agregadoData: LiveData<List<AgregadosData>> = _agregadoData

    private val _beneficiarioData = MutableLiveData<List<BeneficiarioData>>()
    val beneficiarioData: LiveData<List<BeneficiarioData>> = _beneficiarioData

    private val _ticketData = MutableLiveData<List<Tickets>>()
    val ticketData: LiveData<List<Tickets>> = _ticketData

    private val _firestoreState = MutableLiveData<FirestoreState>()
    val firestoreState: LiveData<FirestoreState> = _firestoreState

    fun saveLojaSocialToFirestore(uid: String) {
        val lojaSocialMap = mapOf(
            "nome" to "",
            "descricao" to "",
            "status" to "Pendente",
            "imagemUrl" to ""
        )

        firestore.collection("lojaSocial").document(uid).set(lojaSocialMap)
            .addOnSuccessListener {
                _firestoreState.value = FirestoreState.Success
            }
            .addOnFailureListener { e ->
                _firestoreState.value =
                    FirestoreState.Error("Erro ao salvar dados da Loja Social: ${e.message}")
            }
    }

    private fun generateUniqueId(document: String, onComplete: (String) -> Unit) {
        val counterRef = firestore.collection("counters").document(document)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(counterRef)
            val currentCount = if (snapshot.exists()) {
                snapshot.getLong("count") ?: 0
            } else {
                0
            }

            val newCount = currentCount + 1
            transaction.set(counterRef, mapOf("count" to newCount)) // Atualiza ou cria o contador
            newCount
        }.addOnSuccessListener { newId ->
            onComplete(newId.toString())
        }.addOnFailureListener { e ->
            Log.e("FirestoreViewModel", "Erro ao gerar ID: ${e.message}")
            _firestoreState.value = FirestoreState.Error("Erro ao gerar ID: ${e.message}")
        }
    }

    fun saveAgregado(numdoc: String, dataCriacao: FieldValue, lojaSocialID: String) {

        generateUniqueId("agregados_counter") { uniqueId ->

            val agregadoMap = mapOf(
                "num_doc" to numdoc,
                "data_criacao" to dataCriacao,
                "lojaSocialID" to lojaSocialID
            )

            firestore.collection("agregados").document(uniqueId).set(agregadoMap)
                .addOnSuccessListener {
                    _firestoreState.value = FirestoreState.Success
                }
                .addOnFailureListener { e ->
                    _firestoreState.value =
                        FirestoreState.Error("Erro ao guardar dados do agregado: ${e.message}")
                }
        }
    }

    fun saveBeneficiario(
        telemovel: String,
        nome: String,
        nacionalidade: String,
        agregadoID: String
    ) {

        generateUniqueId("beneficiarios_counter") { uniqueId ->

            val BeneficiarioMap = mapOf(
                "telemovel" to telemovel,
                "nome" to nome,
                "nacionalidade" to nacionalidade,
                "agregado_id" to agregadoID,
                "limite" to false
            )

            firestore.collection("beneficiarios").document(uniqueId).set(BeneficiarioMap)
                .addOnSuccessListener {
                    _firestoreState.value = FirestoreState.Success
                }
                .addOnFailureListener { e ->
                    _firestoreState.value =
                        FirestoreState.Error("Erro ao guardar dados do beneficiario: ${e.message}")
                }
        }
    }


    fun saveAnnounce(
        titulo: String,
        motivo: String,
        meta: String,
        necessidades: String,
        descricao: String,
        link: String,
        requisitos: String,
        lojaSocial: String,
        dataCriacao: FieldValue,
        tipoanuncio: Int,
        imageURL: String
    ) {
        if (lojaSocial.isBlank()) {
            _firestoreState.value =
                FirestoreState.Error("Precisa de estar logado para poder criar anuncio")
            return
        }

        generateUniqueId("announcements_counter") { uniqueId ->

            val announceMap = when (tipoanuncio) {
                0 -> mapOf(
                    "titulo" to titulo,
                    "motivo" to motivo,
                    "meta" to meta,
                    "tipo" to "Doação monetária",
                    "loja_social_id" to lojaSocial,
                    "creation_date" to dataCriacao,
                    "imagemUrl" to imageURL
                )

                1 -> mapOf(
                    "titulo" to titulo,
                    "necessidades" to necessidades,
                    "tipo" to "Doação de bens",
                    "loja_social_id" to lojaSocial,
                    "creation_date" to dataCriacao,
                    "imagemUrl" to imageURL
                )

                2 -> mapOf(
                    "titulo" to titulo,
                    "descricao" to descricao,
                    "link" to link,
                    "tipo" to "Noticia",
                    "loja_social_id" to lojaSocial,
                    "creation_date" to dataCriacao,
                    "imagemUrl" to imageURL
                )

                3 -> mapOf(
                    "titulo" to titulo,
                    "requisitos" to requisitos,
                    "tipo" to "Voluntariado",
                    "loja_social_id" to lojaSocial,
                    "creation_date" to dataCriacao,
                    "imagemUrl" to imageURL
                )

                else -> emptyMap()
            }

            firestore.collection("anuncios").document(uniqueId).set(announceMap)
                .addOnSuccessListener {
                    _firestoreState.value = FirestoreState.Success
                }
                .addOnFailureListener { e ->
                    _firestoreState.value =
                        FirestoreState.Error("Erro ao guardar dados do anúncio: ${e.message}")
                }
        }
    }

    fun saveTicket(
        nome: String,
        email: String,
        motivo: String,
        listabens: String,
        quantidade: String,
        condicao: String,
        descricao: String,
        anuncioId: String,
        tipoAnuncio: String,
        dataCriacao: FieldValue,
        imagemUrl: String?,
        tituloAnuncio: String?
    ) {
        generateUniqueId("tickets_counter") { uniqueId ->

            val ticketMap = when (tipoAnuncio) {
                "Voluntariado" -> mapOf(
                    "nome" to nome,
                    "email" to email,
                    "motivo" to motivo,
                    "tipo" to "Voluntario",
                    "anuncio_id" to anuncioId,
                    "creation_date" to dataCriacao,
                    "anuncio_titulo" to tituloAnuncio,
                    "status" to "Pendente"
                )

                "Doação de bens" -> mapOf(
                    "listabens" to listabens,
                    "quantidade" to quantidade,
                    "condicao" to condicao,
                    "descricao" to descricao,
                    "tipo" to "Bens",
                    "anuncio_id" to anuncioId,
                    "creation_date" to dataCriacao,
                    "imagemUrl" to imagemUrl,
                    "anuncio_titulo" to tituloAnuncio,
                    "status" to "Pendente"
                )

                else -> emptyMap()
            }

            firestore.collection("tickets").document(uniqueId).set(ticketMap)
                .addOnSuccessListener {
                    _firestoreState.value = FirestoreState.Success
                }
                .addOnFailureListener { e ->
                    _firestoreState.value =
                        FirestoreState.Error("Erro ao guardar dados do ticket: ${e.message}")
                }
        }
    }

    // Função para atualizar dados da Loja Social
    fun updateLojaSocialDetails(uid: String, nome: String, descricao: String, imageURL: String) {
        val updatedData = mapOf(
            "nome" to nome,
            "descricao" to descricao,
            "imagemUrl" to imageURL
        )

        firestore.collection("lojaSocial").document(uid).update(updatedData)
            .addOnSuccessListener {
                _firestoreState.value = FirestoreState.Success
            }
            .addOnFailureListener { e ->
                _firestoreState.value =
                    FirestoreState.Error("Erro ao atualizar dados da Loja Social: ${e.message}")
            }
    }

    fun updateAgregadoDetails(id: String, num_doc: String, creationDate: Date, loja_id: String) {
        val agregado = mapOf(
            "num_doc" to num_doc,
            "data_criacao" to creationDate,
            "lojaSocialID" to loja_id
        )

        firestore.collection("agregados").document(id).update(agregado)
            .addOnSuccessListener {
                _firestoreState.value = FirestoreState.Success
            }
            .addOnFailureListener { e ->
                _firestoreState.value =
                    FirestoreState.Error("Erro ao atualizar dados da Loja Social: ${e.message}")
            }
    }

    fun updateBeneficiarioDetails(
        id: String,
        nome: String,
        telemovel: String,
        nacionalidade: String
    ) {
        val beneficiarioUpdated = mapOf(
            "nome" to nome,
            "telemovel" to telemovel,
            "nacionalidade" to nacionalidade
        )

        firestore.collection("beneficiarios").document(id).update(beneficiarioUpdated)
            .addOnSuccessListener {
                _firestoreState.value = FirestoreState.Success
            }
            .addOnFailureListener { e ->
                _firestoreState.value =
                    FirestoreState.Error("Erro ao atualizar dados da Loja Social: ${e.message}")
            }
    }

    fun acceptTicket(
        ticketId: String,
        nome: String,
        email: String,
        motivo: String,
        listabens: String,
        quantidade: String,
        condicao: String,
        descricao: String,
        anuncioId: String,
        tipoAnuncio: String,
        dataCriacao: Date,
        imagemUrl: String?,
        tituloAnuncio: String?
    ) {

        val updatedData = when (tipoAnuncio) {
            "Voluntario" -> mapOf(
                "nome" to nome,
                "email" to email,
                "motivo" to motivo,
                "tipo" to "Voluntario",
                "anuncio_id" to anuncioId,
                "creation_date" to dataCriacao,
                "anuncio_titulo" to tituloAnuncio,
                "status" to "Aprovado"
            )

            "Bens" -> mapOf(
                "listabens" to listabens,
                "quantidade" to quantidade,
                "condicao" to condicao,
                "descricao" to descricao,
                "tipo" to "Bens",
                "anuncio_id" to anuncioId,
                "creation_date" to dataCriacao,
                "imagemUrl" to imagemUrl,
                "anuncio_titulo" to tituloAnuncio,
                "status" to "Aprovado"
            )

            else -> emptyMap()
        }

        firestore.collection("tickets").document(ticketId).update(updatedData)
            .addOnSuccessListener {
                _firestoreState.value = FirestoreState.Success
                getTicketDetails()
            }
            .addOnFailureListener { e ->
                _firestoreState.value =
                    FirestoreState.Error("Erro ao atualizar dados da Loja Social: ${e.message}")
            }

    }

    // Função para atualizar dados do anuncio
    fun updateAnuncioDetails(
        announceID: String,
        titulo: String,
        motivo: String,
        meta: String,
        necessidades: String,
        descricao: String,
        link: String,
        requisitos: String,
        lojaSocial: String,
        dataCriacao: Date?,
        tipo: String,
        imageURL: String
    ) {
        val announceMap = when (tipo) {
            "Doação monetária" -> mapOf(
                "titulo" to titulo,
                "motivo" to motivo,
                "meta" to meta,
                "tipo" to "Doação monetária",
                "loja_social_id" to lojaSocial,
                "creation_date" to dataCriacao,
                "imagemUrl" to imageURL
            )

            "Doação de bens" -> mapOf(
                "titulo" to titulo,
                "necessidades" to necessidades,
                "tipo" to "Doação de bens",
                "loja_social_id" to lojaSocial,
                "creation_date" to dataCriacao,
                "imagemUrl" to imageURL
            )

            "Noticia" -> mapOf(
                "titulo" to titulo,
                "descricao" to descricao,
                "link" to link,
                "tipo" to "Noticia",
                "loja_social_id" to lojaSocial,
                "creation_date" to dataCriacao,
                "imagemUrl" to imageURL
            )

            "Voluntariado" -> mapOf(
                "titulo" to titulo,
                "requisitos" to requisitos,
                "tipo" to "Voluntariado",
                "loja_social_id" to lojaSocial,
                "creation_date" to dataCriacao,
                "imagemUrl" to imageURL
            )

            else -> emptyMap()
        }

        firestore.collection("anuncios").document(announceID).update(announceMap)
            .addOnSuccessListener {
                _firestoreState.value = FirestoreState.Success
            }
            .addOnFailureListener { e ->
                _firestoreState.value =
                    FirestoreState.Error("Erro ao atualizar dados da Loja Social: ${e.message}")
            }
    }

    fun uploadLojaSocialImageToFirebase(imageUri: String, onComplete: (String) -> Unit) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("LojaSocialLogos/${UUID.randomUUID()}")
        val uploadTask = storageRef.putFile(Uri.parse(imageUri))

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onComplete(downloadUri.toString())
            } else {
                Log.e(
                    "FirestoreViewModel",
                    "Erro ao fazer upload da imagem: ${task.exception?.message}"
                )
            }
        }
    }

    fun uploadAnuncioPhotoToFirebase(imageUri: String, onComplete: (String) -> Unit) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("AnuncioPhotos/${UUID.randomUUID()}")
        val uploadTask = storageRef.putFile(Uri.parse(imageUri))

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onComplete(downloadUri.toString())
            } else {
                Log.e(
                    "FirestoreViewModel",
                    "Erro ao fazer upload da imagem: ${task.exception?.message}"
                )
            }
        }
    }

    fun uploadBensPhotoToFirebase(imageUri: String, onComplete: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("Bens/${UUID.randomUUID()}")
        val uploadTask = storageRef.putFile(Uri.parse(imageUri))

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onComplete(downloadUri.toString())
            } else {
                Log.e(
                    "FirestoreViewModel",
                    "Erro ao fazer upload da imagem: ${task.exception?.message}"
                )
            }
        }
    }

    fun getAgregadosByLojaSocialId(lojaSocialId: String) {
        firestore.collection("agregados")
            .whereEqualTo("lojaSocialID", lojaSocialId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val agregados = querySnapshot.documents.mapNotNull { document ->
                    val id = document.id
                    val num_doc = document.getString("num_doc") ?: return@mapNotNull null
                    val loja_id = document.getString("lojaSocialID") ?: return@mapNotNull null
                    val creationDate = document.getDate("data_criacao") ?: return@mapNotNull null

                    AgregadosData(
                        id = id,
                        num_doc = num_doc,
                        data_criacao = creationDate,
                        loja_id = loja_id,
                    )
                }
                _agregadoData.postValue(agregados)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao buscar anúncios: ${e.message}")
                _firestoreState.postValue(FirestoreState.Error("Erro ao buscar anúncios: ${e.message}"))
            }
    }

    fun getSpecificAgregadoDetails(agregado_id: String) {
        firestore.collection("agregados").document(agregado_id).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {

                    val id = documentSnapshot.id
                    val num_doc = documentSnapshot.getString("num_doc") ?: run {
                        Log.e("FirestoreViewModel", "Campo 'num_doc' não encontrado no documento")
                        return@addOnSuccessListener
                    }
                    val loja_id = documentSnapshot.getString("lojaSocialID") ?: run {
                        Log.e(
                            "FirestoreViewModel",
                            "Campo 'lojaSocialID' não encontrado no documento"
                        )
                        return@addOnSuccessListener
                    }
                    val creationDate = documentSnapshot.getDate("data_criacao") ?: run {
                        Log.e(
                            "FirestoreViewModel",
                            "Campo 'data_criacao' não encontrado no documento"
                        )
                        return@addOnSuccessListener
                    }

                    val agregado = AgregadosData(
                        id = id,
                        num_doc = num_doc,
                        data_criacao = creationDate,
                        loja_id = loja_id
                    )

                    _agregadoData.postValue(listOf(agregado))
                } else {
                    Log.e("FirestoreViewModel", "Documento não encontrado no Firestore")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao buscar documento: ${e.message}")
            }
    }

    fun getSpecificBeneficiarioDetails(beneficiarioID: String) {
        firestore.collection("beneficiarios").document(beneficiarioID).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {

                    val id = documentSnapshot.id
                    val agregado_id = documentSnapshot.getString("agregado_id") ?: run {
                        Log.e(
                            "FirestoreViewModel",
                            "Campo 'agregado_id' não encontrado no documento"
                        )
                        return@addOnSuccessListener
                    }
                    val nome = documentSnapshot.getString("nome") ?: run {
                        Log.e(
                            "FirestoreViewModel",
                            "Campo 'nacionalidade' não encontrado no documento"
                        )
                        return@addOnSuccessListener
                    }
                    val telemovel = documentSnapshot.getString("telemovel") ?: run {
                        Log.e("FirestoreViewModel", "Campo 'telemovel' não encontrado no documento")
                        return@addOnSuccessListener
                    }
                    val nacionalidade = documentSnapshot.getString("nacionalidade") ?: run {
                        Log.e("FirestoreViewModel", "Campo 'telemovel' não encontrado no documento")
                        return@addOnSuccessListener
                    }
                    val limite = documentSnapshot.getBoolean("limite") ?: run {
                        Log.e("FirestoreViewModel", "Campo 'limite' não encontrado no documento")
                        return@addOnSuccessListener
                    }

                    val beneficiario = BeneficiarioData(
                        id = id,
                        agregado_id = agregado_id,
                        nome = nome,
                        telemovel = telemovel,
                        nacionalidade = nacionalidade,
                        limite = limite
                    )

                    _beneficiarioData.postValue(listOf(beneficiario))
                } else {
                    Log.e("FirestoreViewModel", "Documento não encontrado no Firestore")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao buscar documento: ${e.message}")
            }
    }

    fun getBeneficiariosByAgregado(agregadoID: String) {
        firestore.collection("beneficiarios")
            .whereEqualTo("agregado_id", agregadoID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val beneficiarios = querySnapshot.documents.mapNotNull { document ->
                    val id = document.id
                    val nacionalidade =
                        document.getString("nacionalidade") ?: return@mapNotNull null
                    val nome = document.getString("nome") ?: return@mapNotNull null
                    val telemovel = document.getString("telemovel") ?: return@mapNotNull null
                    val agregado_id = document.getString("agregado_id") ?: return@mapNotNull null
                    val limite = document.getBoolean("limite") ?: return@mapNotNull null

                    BeneficiarioData(
                        id = id,
                        agregado_id = agregado_id,
                        nome = nome,
                        telemovel = telemovel,
                        nacionalidade = nacionalidade,
                        limite = limite
                    )
                }
                _beneficiarioData.postValue(beneficiarios)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao buscar anúncios: ${e.message}")
                _firestoreState.postValue(FirestoreState.Error("Erro ao buscar anúncios: ${e.message}"))
            }
    }

    fun getBeneficiariosFromLojaSocial(uid: String) {
        firestore.collection("agregados")
            .whereEqualTo("lojaSocialID", uid)
            .get()
            .addOnSuccessListener { agregadosSnapshot ->
                val agregadoIds = agregadosSnapshot.documents.mapNotNull { it.id }

                if (agregadoIds.isEmpty()) {
                    Log.w(
                        "FirestoreViewModel",
                        "Nenhum agregado encontrado para lojaSocialID: $uid"
                    )
                    _beneficiarioData.postValue(emptyList())
                    return@addOnSuccessListener
                }

                firestore.collection("beneficiarios")
                    .whereIn("agregado_id", agregadoIds)
                    .get()
                    .addOnSuccessListener { beneficiariosSnapshot ->
                        val beneficiarios = beneficiariosSnapshot.documents.mapNotNull { document ->
                            val id = document.id
                            val nacionalidade =
                                document.getString("nacionalidade") ?: return@mapNotNull null
                            val nome = document.getString("nome") ?: return@mapNotNull null
                            val telemovel =
                                document.getString("telemovel") ?: return@mapNotNull null
                            val agregado_id =
                                document.getString("agregado_id") ?: return@mapNotNull null
                            val limite = document.getBoolean("limite") ?: return@mapNotNull null

                            BeneficiarioData(
                                id = id,
                                agregado_id = agregado_id,
                                nome = nome,
                                telemovel = telemovel,
                                nacionalidade = nacionalidade,
                                limite = limite
                            )
                        }
                        _beneficiarioData.postValue(beneficiarios)
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreViewModel", "Erro ao buscar beneficiários: ${e.message}")
                        _firestoreState.postValue(FirestoreState.Error("Erro ao buscar beneficiários: ${e.message}"))
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao buscar agregados: ${e.message}")
                _firestoreState.postValue(FirestoreState.Error("Erro ao buscar agregados: ${e.message}"))
            }
    }

    fun getAnunciosByLojaSocialId(lojaSocialId: String) {
        firestore.collection("anuncios")
            .whereEqualTo("loja_social_id", lojaSocialId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val anuncios = querySnapshot.documents.mapNotNull { document ->
                    val id = document.id
                    val titulo = document.getString("titulo") ?: return@mapNotNull null
                    val tipo = document.getString("tipo") ?: return@mapNotNull null
                    val imageUrl = document.getString("imagemUrl")
                    val creationDate = document.getDate("creation_date") ?: return@mapNotNull null

                    AnuncioData(
                        id = id,
                        titulo = titulo,
                        motivo = "",
                        meta = "",
                        necessidades = "",
                        descricao = "",
                        link = "",
                        requisitos = "",
                        lojaSocial = lojaSocialId,
                        data_criacao = creationDate,
                        tipo = tipo,
                        imagemUrl = imageUrl
                    )
                }
                _anuncioData.postValue(anuncios)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao buscar anúncios: ${e.message}")
                _firestoreState.postValue(FirestoreState.Error("Erro ao buscar anúncios: ${e.message}"))
            }
    }

    fun getAllAnunciosWithLojaDetails() {
        firestore.collection("anuncios")
            .orderBy("creation_date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { anunciosSnapshot ->
                val anuncios = mutableListOf<AllAnuncios>()

                val lojaIds =
                    anunciosSnapshot.documents.mapNotNull { it.getString("loja_social_id") }
                        .distinct()

                if (lojaIds.isEmpty()) {
                    _allAnuncios.value = emptyList()
                    return@addOnSuccessListener
                }

                firestore.collection("lojaSocial")
                    .whereIn(FieldPath.documentId(), lojaIds)
                    .get()
                    .addOnSuccessListener { lojasSnapshot ->
                        val lojasMap = lojasSnapshot.documents.associateBy(
                            { it.id },
                            {
                                Pair(
                                    it.getString("nome") ?: "Loja desconhecida",
                                    it.getString("imagemUrl")
                                )
                            }
                        )

                        for (anuncioDoc in anunciosSnapshot.documents) {
                            val lojaSocialId = anuncioDoc.getString("loja_social_id")
                            val lojaDetails = lojasMap[lojaSocialId]

                            val anuncio = AllAnuncios(
                                id = anuncioDoc.id,
                                tipo = anuncioDoc.getString("tipo") ?: "",
                                titulo = anuncioDoc.getString("titulo") ?: "",
                                imagemUrl = anuncioDoc.getString("imagemUrl"),
                                dataCriacao = anuncioDoc.getDate("creation_date") ?: Date(),
                                motivo = anuncioDoc.getString("motivo") ?: "",
                                necessidades = anuncioDoc.getString("necessidades") ?: "",
                                descricao = anuncioDoc.getString("descricao") ?: "",
                                meta = anuncioDoc.getString("meta") ?: "0",
                                link = anuncioDoc.getString("link") ?: "",
                                requisitos = anuncioDoc.getString("requisitos") ?: "",
                                lojaSocialName = lojaDetails?.first ?: "Loja desconhecida",
                                imageUrlLojaSocial = lojaDetails?.second
                            )
                            anuncios.add(anuncio)
                        }

                        // Atualiza o LiveData com os anúncios
                        _allAnuncios.value = anuncios
                    }
                    .addOnFailureListener { e ->
                        Log.e(
                            "FirestoreViewModel",
                            "Erro ao buscar detalhes das lojas: ${e.message}"
                        )
                        _allAnuncios.value = emptyList()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao buscar anúncios: ${e.message}")
                _allAnuncios.value = emptyList()
            }
    }

        fun getLojaSocialDetails(uid: String) {
        firestore.collection("lojaSocial").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nome = document.getString("nome") ?: ""
                    val descricao = document.getString("descricao") ?: ""
                    val imagemUrl = document.getString("imagemUrl")
                    _lojaSocialData.value = LojaSocialData(nome, descricao, imagemUrl)
                } else {
                    _firestoreState.value = FirestoreState.Error("Loja Social não encontrada")
                }
            }
            .addOnFailureListener { e ->
                _firestoreState.value =
                    FirestoreState.Error("Erro ao carregar dados da Loja Social: ${e.message}")
            }
    }

    fun getAnuncioDetails(announceId: String) {
        firestore.collection("anuncios").document(announceId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val id = document.id
                    val titulo = document.getString("titulo") ?: ""
                    val motivo = document.getString("motivo") ?: ""
                    val meta = document.getString("meta") ?: ""
                    val necessidades = document.getString("necessidades") ?: ""
                    val descricao = document.getString("descricao") ?: ""
                    val link = document.getString("link") ?: ""
                    val requisitos = document.getString("requisitos") ?: ""
                    val lojaSocial = document.getString("lojaSocial") ?: ""
                    val dataCriacao = document.getDate("data_criacao") ?: Date()
                    val tipo = document.getString("tipo") ?: ""
                    val imagemUrl = document.getString("imagemUrl")

                    val anuncio = AnuncioData(
                        id = id,
                        titulo = titulo,
                        motivo = motivo,
                        meta = meta,
                        necessidades = necessidades,
                        descricao = descricao,
                        link = link,
                        requisitos = requisitos,
                        lojaSocial = lojaSocial,
                        data_criacao = dataCriacao,
                        tipo = tipo,
                        imagemUrl = imagemUrl
                    )

                    _anuncioData.value = listOf(anuncio)
                } else {
                    _firestoreState.value = FirestoreState.Error("Anúncio não encontrado")
                }
            }
            .addOnFailureListener { e ->
                _firestoreState.value =
                    FirestoreState.Error("Erro ao carregar dados do anúncio: ${e.message}")
            }
    }

    fun getTicketDetails() {
        firestore.collection("tickets")
            .whereEqualTo("status", "Pendente")
            .get()
            .addOnSuccessListener { documents ->
                val tickets = documents.mapNotNull { document ->
                    val id = document.id
                    val anuncio_id = document.getString("anuncio_id") ?: ""
                    val anuncio_titulo = document.getString("anuncio_titulo") ?: ""
                    val motivo = document.getString("motivo") ?: ""
                    val email = document.getString("email") ?: ""
                    val creation_date = document.getDate("creation_date") ?: Date()
                    val nome = document.getString("nome") ?: ""
                    val tipo = document.getString("tipo") ?: ""
                    val condicao = document.getString("condicao") ?: ""
                    val descricao = document.getString("descricao") ?: ""
                    val imagemUrl = document.getString("imagemUrl") ?: ""
                    val listaBens = document.getString("listabens") ?: ""
                    val quantidade = document.getString("quantidade") ?: ""
                    val status = document.getString("status") ?: ""
                    Tickets(
                        id,
                        anuncio_id,
                        anuncio_titulo,
                        motivo,
                        email,
                        creation_date,
                        nome,
                        tipo,
                        condicao,
                        descricao,
                        imagemUrl,
                        listaBens,
                        quantidade,
                        status
                    )
                }
                _ticketData.value = tickets
            }
            .addOnFailureListener { e ->
                _firestoreState.value =
                    FirestoreState.Error("Erro ao carregar dados dos tickets: ${e.message}")
            }
    }


    fun deleteAnuncio(anuncioId: String) {
        firestore.collection("anuncios").document(anuncioId)
            .delete()
            .addOnSuccessListener {
                // Atualiza a lista de anúncios após apagar
                getAnunciosByLojaSocialId(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao apagar anúncio: ${e.message}")
                _firestoreState.value = FirestoreState.Error("Erro ao apagar anúncio: ${e.message}")
            }
    }

    fun deleteBeneficiario(beneficiarioID: String) {
        firestore.collection("beneficiarios").document(beneficiarioID)
            .delete()
            .addOnSuccessListener {
                getBeneficiariosByAgregado(beneficiarioID)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao apagar beneficiario: ${e.message}")
                _firestoreState.value = FirestoreState.Error("Erro ao apagar anúncio: ${e.message}")
            }
    }

    fun deleteAgregado(AgregadoID: String) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("agregados").document(AgregadoID)
            .delete()
            .addOnSuccessListener {
                firestore.collection("beneficiarios")
                    .whereEqualTo("agregado_id", AgregadoID)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val batch = firestore.batch()
                        for (document in querySnapshot.documents) {
                            batch.delete(document.reference)
                        }

                        batch.commit()
                            .addOnSuccessListener {
                                Log.d("FirestoreViewModel", "Beneficiários apagados com sucesso.")
                                getTicketDetails()
                            }
                            .addOnFailureListener { e ->
                                Log.e(
                                    "FirestoreViewModel",
                                    "Erro ao apagar Beneficiários: ${e.message}"
                                )
                                _firestoreState.value =
                                    FirestoreState.Error("Erro ao apagar Beneficiários: ${e.message}")
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreViewModel", "Erro ao buscar Beneficiários: ${e.message}")
                        _firestoreState.value =
                            FirestoreState.Error("Erro ao buscar Beneficiários: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao apagar Agregado: ${e.message}")
                _firestoreState.value =
                    FirestoreState.Error("Erro ao apagar Agregado: ${e.message}")
            }
    }

    fun deleteTicket(ticketId: String) {
        firestore.collection("tickets").document(ticketId)
            .delete()
            .addOnSuccessListener {
                getTicketDetails()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao apagar anúncio: ${e.message}")
                _firestoreState.value = FirestoreState.Error("Erro ao apagar anúncio: ${e.message}")
            }
    }


    suspend fun fetchBeneficiariosGroupedByNacionalidade(): List<PieEntry> {
        val db = FirebaseFirestore.getInstance()
        val entries = mutableListOf<PieEntry>()

        try {
            val querySnapshot = db.collection("beneficiarios").get().await()

            // Group data by nationality and count the number of beneficiaries
            val nationalityCount = mutableMapOf<String, Int>()

            querySnapshot.documents.forEach { document ->
                val nationality = document.getString("nacionalidade") ?: "Unknown"
                nationalityCount[nationality] = nationalityCount.getOrDefault(nationality, 0) + 1
            }

            // Create PieEntry for each nationality and count
            nationalityCount.forEach { (nationality, count) ->
                entries.add(PieEntry(count.toFloat(), nationality))
            }
        } catch (e: Exception) {
            // Handle error if data fetch fails
        }

        return entries
    }

    fun getBeneficiarioByTelemovel(telemovel: String) {
        firestore.collection("beneficiarios")
            .whereEqualTo("telemovel", telemovel)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0] // Assume que o telemóvel é único
                    val id = document.id
                    val agregadoId = document.getString("agregado_id") ?: ""
                    val nome = document.getString("nome") ?: ""
                    val telemovel = document.getString("telemovel") ?: ""
                    val nacionalidade = document.getString("nacionalidade") ?: ""
                    val limite = document.getBoolean("limite") ?: false

                    val beneficiario = BeneficiarioData(
                        id = id,
                        agregado_id = agregadoId,
                        nome = nome,
                        telemovel = telemovel,
                        nacionalidade = nacionalidade,
                        limite = limite
                    )

                    // Atualiza o LiveData com os dados do beneficiário encontrado
                    _beneficiarioData.value = listOf(beneficiario)
                } else {
                    // Beneficiário não encontrado
                    _beneficiarioData.value = emptyList()
                    _firestoreState.value = FirestoreState.Error("Nenhum beneficiário encontrado com o telemóvel fornecido.")
                }
            }
            .addOnFailureListener { e ->
                _firestoreState.value = FirestoreState.Error("Erro ao buscar beneficiário: ${e.message}")
            }
    }


    fun saveVisita(id_beneficiario: String, comportamento: String, motivo: String, produto_recolhido: String) {
        generateUniqueId("visita_counter"){ uniqueId ->

            val visitaMap = mapOf(
                "id_beneficiario" to id_beneficiario,
                "comportamento" to comportamento,
                "motivo" to motivo,
                "produto_recolhido" to produto_recolhido
            )

            firestore.collection("visita").document(uniqueId).set(visitaMap)
                .addOnSuccessListener {
                    _firestoreState.value = FirestoreState.Success
                }
                .addOnFailureListener { e ->
                    _firestoreState.value = FirestoreState.Error("Erro ao guardar dados da Visita: ${e.message}")
                }
        }
    }

    fun updateBeneficiarioLimite(beneficiarioId: String, novoLimite: Boolean) {
        val updatedData = mapOf(
            "limite" to novoLimite
        )

        firestore.collection("beneficiarios").document(beneficiarioId).update(updatedData)
            .addOnSuccessListener {
                _firestoreState.value = FirestoreState.Success
            }
            .addOnFailureListener { e ->
                _firestoreState.value = FirestoreState.Error("Erro ao atualizar o campo limite do beneficiário: ${e.message}")
            }
    }

    fun getNumeroDeVisitas(beneficiarioID: String, onResult: (Int) -> Unit) {
        firestore.collection("visita")
            .whereEqualTo("id_beneficiario", beneficiarioID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onResult(querySnapshot.size()) // Conta o número de documentos
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar número de visitas: ${e.message}")
                onResult(0) // Retorna 0 em caso de erro
            }
    }

    fun getVisitasByBeneficiario(
        beneficiarioID: String,
        onResult: (List<Pair<String, String>>, List<String>) -> Unit
    ) {
        firestore.collection("visita")
            .whereEqualTo("id_beneficiario", beneficiarioID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val comportamentos = mutableListOf<Pair<String, String>>()
                val produtosRecolhidos = mutableListOf<String>()

                querySnapshot.documents.forEach { document ->
                    val comportamento = document.getString("comportamento") ?: ""
                    val motivo = document.getString("motivo") ?: "N/A"
                    val produto = document.getString("produto_recolhido") ?: ""

                    // Apenas adiciona comportamentos não vazios
                    if (comportamento.isNotEmpty()) {
                        comportamentos.add(comportamento to motivo)
                    }

                    // Apenas adiciona produtos não vazios
                    if (produto.isNotEmpty()) {
                        produtosRecolhidos.add(produto)
                    }
                }

                onResult(comportamentos, produtosRecolhidos)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar visitas: ${e.message}")
                onResult(emptyList(), emptyList())
            }
    }



}


sealed class FirestoreState {
    data object Success : FirestoreState()
    data class Error(val message: String) : FirestoreState()
}

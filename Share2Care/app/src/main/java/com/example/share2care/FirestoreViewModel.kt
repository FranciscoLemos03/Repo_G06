package com.example.share2care

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Date
import java.util.UUID

class FirestoreViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    data class LojaSocialData(val nome: String, val descricao: String, val imagemUrl: String? = null)
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
        val requisitos:String = "",
        val lojaSocialName: String = "",
        val imageUrlLojaSocial: String? = null
    )
    data class Tickets(
        val id: String = "",
        val anuncio_id: String = "",
        val anuncio_titulo: String = "",
        val motivo: String = "",
        val email: String? = null,
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

    private val _lojaSocialData = MutableLiveData<LojaSocialData>()
    val lojaSocialData: LiveData<LojaSocialData> = _lojaSocialData

    private val _anuncioData = MutableLiveData<List<AnuncioData>>()
    val anuncioData: LiveData<List<AnuncioData>> = _anuncioData

    private val _allAnuncios = MutableLiveData<List<AllAnuncios>>()
    val allAnuncios: LiveData<List<AllAnuncios>> get() = _allAnuncios

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
                _firestoreState.value = FirestoreState.Error("Erro ao salvar dados da Loja Social: ${e.message}")
            }
    }

    private fun generateUniqueId(onComplete: (String) -> Unit) {
        val counterRef = firestore.collection("counters").document("announcements_counter")

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

    private fun generateUniqueIdTickets(onComplete: (String) -> Unit) {
        val counterRef = firestore.collection("counters").document("tickets_counter")

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(counterRef)
            val currentCount = if (snapshot.exists()) {
                snapshot.getLong("count") ?: 0
            } else {
                0
            }

            val newCount = currentCount + 1
            transaction.set(counterRef, mapOf("count" to newCount))
            newCount
        }.addOnSuccessListener { newId ->
            onComplete(newId.toString())
        }.addOnFailureListener { e ->
            Log.e("FirestoreViewModel", "Erro ao gerar ID: ${e.message}")
            _firestoreState.value = FirestoreState.Error("Erro ao gerar ID: ${e.message}")
        }
    }

    fun saveAnnounce(
        titulo: String, motivo: String, meta: String, necessidades: String, descricao: String,
        link: String, requisitos: String, lojaSocial: String, dataCriacao: FieldValue, tipoanuncio: Int, imageURL: String
    ) {
        if (lojaSocial.isBlank()) {
            _firestoreState.value = FirestoreState.Error("Precisa de estar logado para poder criar anuncio")
            return
        }

        generateUniqueId{ uniqueId ->

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
                    _firestoreState.value = FirestoreState.Error("Erro ao guardar dados do anúncio: ${e.message}")
                }
        }
    }

    fun saveTicket(
        nome: String, email: String, motivo: String, listabens: String, quantidade: String,
        condicao: String, descricao: String, anuncioId: String, tipoAnuncio: String ,dataCriacao: FieldValue, imagemUrl: String?, tituloAnuncio: String?
    ) {
        generateUniqueIdTickets{ uniqueId ->

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
                    _firestoreState.value = FirestoreState.Error("Erro ao guardar dados do ticket: ${e.message}")
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
                _firestoreState.value = FirestoreState.Error("Erro ao atualizar dados da Loja Social: ${e.message}")
            }
    }

    // Função para atualizar dados do anuncio
    fun updateAnuncioDetails(announceID: String, titulo: String, motivo: String, meta: String, necessidades: String, descricao: String, link: String, requisitos: String, lojaSocial: String, dataCriacao: Date?, tipo: String, imageURL: String) {
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
                _firestoreState.value = FirestoreState.Error("Erro ao atualizar dados da Loja Social: ${e.message}")
            }
    }

    fun uploadLojaSocialImageToFirebase(imageUri: String, onComplete: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("LojaSocialLogos/${UUID.randomUUID()}")
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
                Log.e("FirestoreViewModel", "Erro ao fazer upload da imagem: ${task.exception?.message}")
            }
        }
    }

    fun uploadAnuncioPhotoToFirebase(imageUri: String, onComplete: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("AnuncioPhotos/${UUID.randomUUID()}")
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
                Log.e("FirestoreViewModel", "Erro ao fazer upload da imagem: ${task.exception?.message}")
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
                Log.e("FirestoreViewModel", "Erro ao fazer upload da imagem: ${task.exception?.message}")
            }
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

                val lojaIds = anunciosSnapshot.documents.mapNotNull { it.getString("loja_social_id") }.distinct()

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
                            { Pair(it.getString("nome") ?: "Loja desconhecida", it.getString("imagemUrl")) }
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
                        Log.e("FirestoreViewModel", "Erro ao buscar detalhes das lojas: ${e.message}")
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
                _firestoreState.value = FirestoreState.Error("Erro ao carregar dados da Loja Social: ${e.message}")
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
                _firestoreState.value = FirestoreState.Error("Erro ao carregar dados do anúncio: ${e.message}")
            }
    }

    fun getTicketDetails() {
        firestore.collection("tickets").get()
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
                _firestoreState.value = FirestoreState.Error("Erro ao carregar dados dos tickets: ${e.message}")
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
                Log.e("FirestoreViewModel", "Erro ao deletar anúncio: ${e.message}")
                _firestoreState.value = FirestoreState.Error("Erro ao deletar anúncio: ${e.message}")
            }
    }

    fun deleteTicket(ticketId: String) {
        firestore.collection("tickets").document(ticketId)
            .delete()
            .addOnSuccessListener {
                getTicketDetails()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreViewModel", "Erro ao deletar anúncio: ${e.message}")
                _firestoreState.value = FirestoreState.Error("Erro ao deletar anúncio: ${e.message}")
            }
    }

}

sealed class FirestoreState {
    data object Success : FirestoreState()
    data class Error(val message: String) : FirestoreState()
}

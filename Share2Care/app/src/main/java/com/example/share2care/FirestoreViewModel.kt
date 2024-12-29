package com.example.share2care

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class FirestoreViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    data class LojaSocialData(val nome: String, val descricao: String, val imagemUrl: String? = null)
    data class AnuncioData(val titulo: String, val motivo: String, val meta: String, val necessidades: String, val descricao: String, val link: String, val requisitos: String, val lojaSocial: String, val data_criacao: Date, val tipo: String, val imagemUrl: String?)
    data class AllAnuncios(
        val tipo: String = "",
        val titulo: String = "",
        val imagemUrl: String? = null,
        val dataCriacao: Date = Date(),
        val lojaSocialName: String = "",
        val imageUrlLojaSocial: String? = null
    )

    private val _lojaSocialData = MutableLiveData<LojaSocialData>()
    val lojaSocialData: LiveData<LojaSocialData> = _lojaSocialData

    private val _anuncioData = MutableLiveData<List<AnuncioData>>()
    val anuncioData: LiveData<List<AnuncioData>> = _anuncioData

    private val _allAnuncios = MutableLiveData<List<AllAnuncios>>()
    val allAnuncios: LiveData<List<AllAnuncios>> get() = _allAnuncios

    private val _firestoreState = MutableLiveData<FirestoreState>()
    val firestoreState: LiveData<FirestoreState> = _firestoreState

    var numeroAnuncios = mutableStateOf(0)

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

    fun generateUniqueId(onComplete: (String) -> Unit) {
        val counterRef = firestore.collection("counters").document("announcements_counter")

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(counterRef)
            val currentCount = if (snapshot.exists()) {
                snapshot.getLong("count") ?: 0
            } else {
                0 // Valor inicial se o documento não existir
            }

            val newCount = currentCount + 1
            transaction.set(counterRef, mapOf("count" to newCount)) // Atualiza ou cria o contador
            newCount
        }.addOnSuccessListener { newId ->
            onComplete(newId.toString()) // Retorna o ID gerado como String
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

    fun getAnunciosByLojaSocialId(lojaSocialId: String) {
        firestore.collection("anuncios")
            .whereEqualTo("loja_social_id", lojaSocialId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val anuncios = querySnapshot.documents.mapNotNull { document ->
                    val titulo = document.getString("titulo") ?: return@mapNotNull null
                    val tipo = document.getString("tipo") ?: return@mapNotNull null
                    val imageUrl = document.getString("imagemUrl")
                    val creationDate = document.getDate("creation_date") ?: return@mapNotNull null

                    AnuncioData(
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
        firestore.collection("anuncios").get()
            .addOnSuccessListener { anunciosSnapshot ->
                val anuncios = mutableListOf<AllAnuncios>()

                // Obtém os IDs únicos das lojas sociais
                val lojaIds = anunciosSnapshot.documents.mapNotNull { it.getString("loja_social_id") }.distinct()

                if (lojaIds.isEmpty()) {
                    // Se não houver lojas, atualiza o LiveData com uma lista vazia
                    _allAnuncios.value = emptyList()
                    return@addOnSuccessListener
                }

                // Consulta os detalhes das lojas sociais
                firestore.collection("lojasSocial")
                    .whereIn("uid", lojaIds)
                    .get()
                    .addOnSuccessListener { lojasSnapshot ->
                        val lojasMap = lojasSnapshot.documents.associateBy(
                            { it.id },
                            { Pair(it.getString("nome") ?: "", it.getString("imagemUrl")) }
                        )

                        for (anuncioDoc in anunciosSnapshot.documents) {
                            val lojaSocialId = anuncioDoc.getString("loja_social_id")
                            val lojaDetails = lojasMap[lojaSocialId]

                            // Verifica se os detalhes da loja social estão disponíveis
                            val anuncio = AllAnuncios(
                                tipo = anuncioDoc.getString("tipo") ?: "",
                                titulo = anuncioDoc.getString("titulo") ?: "",
                                imagemUrl = anuncioDoc.getString("imagemUrl"),
                                dataCriacao = anuncioDoc.getDate("creation_date") ?: Date(),
                                lojaSocialName = lojaDetails?.first ?: "Loja desconhecida", // Se não encontrar a loja
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
}

sealed class FirestoreState {
    object Success : FirestoreState()
    data class Error(val message: String) : FirestoreState()
}

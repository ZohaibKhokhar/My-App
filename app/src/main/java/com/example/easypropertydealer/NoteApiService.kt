import com.example.easypropertydealer.Note
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NoteApiService {

    @GET("api/Note")
    suspend fun getNotes(): List<Note>

    @POST("api/Note")
    suspend fun addNote(@Body note: Note): Response<Note>

    @PUT("api/Note/{id}")
    suspend fun updateNote(
        @Path("id") noteId: Int,
        @Body updatedNote: Note
    ): Response<Note>

    @DELETE("api/Note/{id}")
    suspend fun deleteNote(
        @Path("id") noteId: Int
    ): Response<Unit>
}

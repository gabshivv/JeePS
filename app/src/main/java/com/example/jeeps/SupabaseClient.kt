package com.example.jeeps
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://radmhhggergkurkbjsmu.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJhZG1oaGdnZXJna3Vya2Jqc211Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzM5OTA3MTEsImV4cCI6MjA4OTU2NjcxMX0.4J2WQ_I3qjUkSssA2Ok1kZAJhJ9ZBhqjbNZas6gli78"
    ) {
        install(Postgrest)
    }
}
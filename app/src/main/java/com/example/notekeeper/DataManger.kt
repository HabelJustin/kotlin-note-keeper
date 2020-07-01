package com.example.notekeeper

object DataManger {
    val courses = HashMap<String, CourseInfo>()
    val notes = ArrayList<NoteInfo>()

    init {
        initializeCourse()
        initializeNotes()
    }


    private fun initializeCourse(){
        var course = CourseInfo("android_intents", "Android Programming with Intents")
        courses.set(course.courseId, course)


        course = CourseInfo("android_async", "Android Async Programming and Services")
        courses.set(course.courseId, course)

        course = CourseInfo("java_lang", "Java Fundamentals: The Java Language")
        courses.set(course.courseId, course)

        course = CourseInfo("java_core", "Java Fundamentals: The Core Platform")
        courses.set(course.courseId, course)
    }

    private fun initializeNotes() {
        var note = NoteInfo(courses["android_intents"]!!, "Dynamic intent resolution", "intent allow components to be resolved at runtime")
        notes.add(note)

        note = NoteInfo(courses["android_async"]!!, "Service default threads", "By default Android Service will tie up the UI thread")
        notes.add(note)

        note = NoteInfo(courses["java_lang"]!!, "Anonymous classes classes", "Simplify implementing one-use types")
        notes.add(note)

        note = NoteInfo(courses["java_core"]!!, "Serialization", "Remember to include SerialVersionUID to assure version compatibility")
        notes.add(note)

        note = NoteInfo(courses["android_async"]!!, "Service default threads", "By default Android Service will tie up the UI thread")
        notes.add(note)

        note = NoteInfo(courses["java_lang"]!!, "Anonymous classes classes", "Simplify implementing one-use types")
        notes.add(note)

        note = NoteInfo(courses["java_core"]!!, "Serialization", "Remember to include SerialVersionUID to assure version compatibility")
        notes.add(note)
    }
}
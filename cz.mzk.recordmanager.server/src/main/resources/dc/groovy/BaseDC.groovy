institution="RecordMan"

recordtype = "dublinCore"
allfields = getAllFields()
fullrecord = getFullrecord()

author = getFirstCreator()
author2 = getOtherCreators()

ibsn = getISBNs()
issn = getISSNs()

//description = getDescriptionText()
title = getFirstTitle()
title_alt = getOtherTitles()
publishDate = getFirstDate()
publisher = getPublishers()

topic = getSubjects()
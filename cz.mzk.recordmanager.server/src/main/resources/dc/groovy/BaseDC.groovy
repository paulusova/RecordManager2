recordtype = "dublinCore"
title = getFirstTitle()

allfields = getAllFields()

author = getFirstCreator()
author_search = getFirstCreator()
author2 = getOtherCreators()

ibsn = getISBNs()
issn = getISSNs()

//description = getDescriptionText()
title = getFirstTitle()
title_alt = getOtherTitles()
publishDate = getFirstDate()
publisher = getPublishers()

topic = getSubjects()

local_statuses_facet_str_mv = getStatuses()
fullrecord = getFullRecord()

authors_dummy = ""
authority_dummy_field = ""
kramerius_dummy_rights = getRights()
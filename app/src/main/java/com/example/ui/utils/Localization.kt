package com.example.ui.utils

import androidx.compose.ui.graphics.Color

object Loc {
    private val translations = mapOf(
        "tab_storyteller" to mapOf(
            "English" to "Storyteller",
            "Russian" to "Сказочник",
            "Spanish" to "Cuentacuentos",
            "Chinese" to "故事家",
            "Hindi" to "कहानीकार",
            "Arabic" to "الحكواتي",
            "French" to "Conteur",
            "Portuguese" to "Contador",
            "German" to "Erzähler",
            "Japanese" to "語り部"
        ),
        "tab_news" to mapOf(
            "English" to "X.com Trends",
            "Russian" to "Тренды X.com",
            "Spanish" to "Tendencias X.com",
            "Chinese" to "X.com趋势",
            "Hindi" to "X.com रुझान",
            "Arabic" to "اتجاهات X.com",
            "French" to "Tendances X.com",
            "Portuguese" to "Tendências X.com",
            "German" to "X.com-Trends",
            "Japanese" to "X.comトレンド"
        ),
        "tab_radio" to mapOf(
            "English" to "Radio",
            "Russian" to "Радио",
            "Spanish" to "Radio",
            "Chinese" to "电台",
            "Hindi" to "रेडियो",
            "Arabic" to "الراديو",
            "French" to "Radio",
            "Portuguese" to "Rádio",
            "German" to "Radio",
            "Japanese" to "ラジオ"
        ),
        "tab_saved" to mapOf(
            "English" to "Saved",
            "Russian" to "Сохранённые",
            "Spanish" to "Guardados",
            "Chinese" to "已保存",
            "Hindi" to "सहेजे गए",
            "Arabic" to "المحفوظة",
            "French" to "Enregistrés",
            "Portuguese" to "Salvos",
            "German" to "Gespeichert",
            "Japanese" to "保存済み"
        ),
        "select_style" to mapOf(
            "English" to "Select storytelling style:",
            "Russian" to "Выберите стиль повествования:",
            "Spanish" to "Selecciona el estilo de narración:",
            "Chinese" to "选择故事风格:",
            "Hindi" to "कहानी शैली चुनें:",
            "Arabic" to "اختر أسلوب سرد القصص:",
            "French" to "Sélectionnez le style de conte:",
            "Portuguese" to "Selecione o estilo de narrativa:",
            "German" to "Erzählstil auswählen:",
            "Japanese" to "語り口調のスタイルを選択:"
        ),
        "choose_situation" to mapOf(
            "English" to "Describe context situation:",
            "Russian" to "Опишите ситуацию (контекст):",
            "Spanish" to "Describa la situación del contexto:",
            "Chinese" to "描述故事背景/情境:",
            "Hindi" to "संदर्भ स्थिति का वर्णन करें:",
            "Arabic" to "صف الموقف (السياق):",
            "French" to "Décrivez la situation de contexte:",
            "Portuguese" to "Descreva a situação do contexto:",
            "German" to "Beschreibe die Situation/Kontext:",
            "Japanese" to "状況（コンテキスト）を説明してください:"
        ),
        "describe_placeholder" to mapOf(
            "English" to "Describe the situation (e.g. at a family dinner, stuck in traffic...)",
            "Russian" to "Опишите ситуацию (например, семейный ужин, в пробке...)",
            "Spanish" to "Describa la situación (por ejemplo, cena familiar, atrapado en el tráfico...)",
            "Chinese" to "描述情境（例如：家庭聚餐、堵车...）",
            "Hindi" to "स्थिति का वर्णन करें (जैसे कि पारिवारिक रात्रिभोज, ट्रैफिक जाम...)",
            "Arabic" to "صف الموقف (مثلاً عشاء عائلي، عالق в الزحام...)",
            "French" to "Décrivez la situation (ex: dîner de famille, coincé dans les bouchons...)",
            "Portuguese" to "Descreva a situação (ex: jantar em família, no trânsito...)",
            "German" to "Beschreibe die Situation (z.B. Familienessen, im Stau...)",
            "Japanese" to "状況を説明してください（例：家族での夕食、渋滞中...）"
        ),
        "start_storytelling" to mapOf(
            "English" to "Start storytelling",
            "Russian" to "Начать повествование",
            "Spanish" to "Comenzar narración",
            "Chinese" to "开始讲故事",
            "Hindi" to "कहानी सुनाना शुरू करें",
            "Arabic" to "ابدأ سرد القصص",
            "French" to "Commencer le conte",
            "Portuguese" to "Iniciar narrativa",
            "German" to "Geschichtenerzählen starten",
            "Japanese" to "語りを聞く"
        ),
        "storytelling_loading" to mapOf(
            "English" to "AI storyteller is consulting with folk elders...",
            "Russian" to "Сказочник советуется с мудрецами...",
            "Spanish" to "El cuentacuentos de IA está consultando con los sabios...",
            "Chinese" to "AI故事家正在向民间长者咨询...",
            "Hindi" to "एआई कहानीकार लोक बुद्धिजीवियों से परामर्श कर रहा है...",
            "Arabic" to "حكواتي الذكاء الاصطناعي يستشير الحكماء...",
            "French" to "Le conteur IA consulte les sages...",
            "Portuguese" to "O contador de histórias de IA está consultando os sábios...",
            "German" to "Der KI-Geschichtenerzähler berät sich mit den Weisen...",
            "Japanese" to "AI語り部が長老たちに相談しています..."
        ),
        "storyteller_intro_label" to mapOf(
            "English" to "Storyteller's Introduction:",
            "Russian" to "Вступление сказочника:",
            "Spanish" to "Introducción del cuentacuentos:",
            "Chinese" to "故事家前言:",
            "Hindi" to "कहानीकार का परिचय:",
            "Arabic" to "مقدمة الحكواتي:",
            "French" to "Introduction du conteur:",
            "Portuguese" to "Introdução do contador de histórias:",
            "German" to "Einleitung des Erzählers:",
            "Japanese" to "語り部の導入部:"
        ),
        "storyteller_outro_label" to mapOf(
            "English" to "Storyteller's Conclusion:",
            "Russian" to "Мудрость сказочника:",
            "Spanish" to "Conclusión del cuentacuentos:",
            "Chinese" to "故事家结语:",
            "Hindi" to "कहानीकार का निष्कर्ष:",
            "Arabic" to "خاتمة الحكواتي:",
            "French" to "Conclusion du conteur:",
            "Portuguese" to "Conclusão do contador de histórias:",
            "German" to "Schlusswort des Erzählers:",
            "Japanese" to "語り部の結び:"
        ),
        "save_joke" to mapOf(
            "English" to "Save Joke",
            "Russian" to "Сохранить анекдот",
            "Spanish" to "Guardar chiste",
            "Chinese" to "保存故事",
            "Hindi" to "कहानी सहेजें",
            "Arabic" to "حفظ النكتة",
            "French" to "Enregistrer la blague",
            "Portuguese" to "Salvar piada",
            "German" to "Witz speichern",
            "Japanese" to "ユーモアを保存"
        ),
        "copy_joke" to mapOf(
            "English" to "Copy Joke",
            "Russian" to "Скопировать",
            "Spanish" to "Copiar chiste",
            "Chinese" to "复制故事",
            "Hindi" to "कहानी कॉपी करें",
            "Arabic" to "نسخ النكتة",
            "French" to "Copier la blague",
            "Portuguese" to "Copiar piada",
            "German" to "Witz kopieren",
            "Japanese" to "コピーする"
        ),
        "joke_copied" to mapOf(
            "English" to "Joke copied!",
            "Russian" to "Анекдот скопирован!",
            "Spanish" to "¡Chiste copiado!",
            "Chinese" to "故事已复制!",
            "Hindi" to "कहानी कॉपी हो गई!",
            "Arabic" to "تم نسخ النكتة!",
            "French" to "Blague copiée !",
            "Portuguese" to "Piada copiada!",
            "German" to "Witz kopiert!",
            "Japanese" to "コピーしました！"
        ),
        "added_favorites" to mapOf(
            "English" to "Added to Favorites!",
            "Russian" to "Добавлено в избранное!",
            "Spanish" to "¡Añadido a favoritos!",
            "Chinese" to "已添加到收藏夹!",
            "Hindi" to "पसंदीदा में जोड़ा गया!",
            "Arabic" to "تم الإضافة إلى المفضلة!",
            "French" to "Ajouté aux favoris !",
            "Portuguese" to "Adicionado aos Favoritos!",
            "German" to "Zu Favoriten hinzugefügt!",
            "Japanese" to "お気に入りに追加しました！"
        ),
        "removed_favorites" to mapOf(
            "English" to "Removed from Favorites!",
            "Russian" to "Удалено из избранного!",
            "Spanish" to "¡Eliminado de favoritos!",
            "Chinese" to "已从收藏夹移除!",
            "Hindi" to "पसंदीदा से हटाया गया!",
            "Arabic" to "تم الحذف من المفضلة!",
            "French" to "Retiré des favoris !",
            "Portuguese" to "Removido dos Favoritos!",
            "German" to "Aus Favoriten entfernt!",
            "Japanese" to "お気に入りから削除しました！"
        ),
        "x_integration" to mapOf(
            "English" to "Integrated X Accounts",
            "Russian" to "Интеграция X-аккаунтов",
            "Spanish" to "Cuentas de X Integradas",
            "Chinese" to "已集成X账号",
            "Hindi" to "एकीकृत X खाते",
            "Arabic" to "حسابات X المتكاملة",
            "French" to "Comptes X intégrés",
            "Portuguese" to "Contas do X Integradas",
            "German" to "Integrierte X-Konten",
            "Japanese" to "統合されたXアカウント"
        ),
        "active_suffix" to mapOf(
            "English" to "active",
            "Russian" to "активно",
            "Spanish" to "activo",
            "Chinese" to "活跃",
            "Hindi" to "सक्रिय",
            "Arabic" to "نشط",
            "French" to "actif",
            "Portuguese" to "ativo",
            "German" to "aktiv",
            "Japanese" to "アクティブ"
        ),
        "add_account_placeholder" to mapOf(
            "English" to "Add account handle (e.g. @NASA)...",
            "Russian" to "Добавить аккаунт (например, @NASA)...",
            "Spanish" to "Añadir cuenta de X (ej., @NASA)...",
            "Chinese" to "添加X账号（如 @NASA）...",
            "Hindi" to "एक्स खाता हैंडल जोड़ें (जैसे @NASA)...",
            "Arabic" to "إضافة حساب (مثال NASA@)...",
            "French" to "Ajouter un compte X (ex: @NASA)...",
            "Portuguese" to "Adicionar conta do X (ex: @NASA)...",
            "German" to "X-Konto hinzufügen (z. B. @NASA)...",
            "Japanese" to "アカウントを追加してください（例：@NASA）..."
        ),
        "add_button" to mapOf(
            "English" to "Add",
            "Russian" to "Добавить",
            "Spanish" to "Añadir",
            "Chinese" to "添加",
            "Hindi" to "जोड़ें",
            "Arabic" to "إضافة",
            "French" to "Ajouter",
            "Portuguese" to "Adicionar",
            "German" to "Hinzufügen",
            "Japanese" to "追加"
        ),
        "search_placeholder" to mapOf(
            "English" to "What's happening? Search topic...",
            "Russian" to "Что происходит? Искать или ввести тему...",
            "Spanish" to "¿Qué está pasando? Buscar tema...",
            "Chinese" to "在发生什么？搜索或输入主题...",
            "Hindi" to "क्या हो रहा है? विषय खोजें या दर्ज करें...",
            "Arabic" to "ما الذي يحدث؟ ابحث عن موضوع...",
            "French" to "Que se passe-t-il ? Rechercher...",
            "Portuguese" to "O que está acontecendo? Buscar tema...",
            "German" to "Was passiert? Thema suchen...",
            "Japanese" to "何が起きていますか？トピックを検索..."
        ),
        "news_loading" to mapOf(
            "English" to "Consulting news API feeds & analyzing folklore matches...",
            "Russian" to "Опрашиваем API и собираем фольклор...",
            "Spanish" to "Consultando feeds de API de noticias y analizando coincidencias de folklore...",
            "Chinese" to "正在查询新闻API并匹配民间传说...",
            "Hindi" to "समाचार एपीआई फीड से परामर्श और लोक कथाओं का विश्लेषण कर रहे हैं...",
            "Arabic" to "جاري الاستعلام من واجهات الأخبار وتحليل تطابق الفولكلور...",
            "French" to "Consultation des flux d'actualités et analyse du folklore...",
            "Portuguese" to "Consultando feeds de API de notícias e analisando o folclore...",
            "German" to "News-API-Feeds abrufen und Folklore-Übereinstimmungen analysieren...",
            "Japanese" to "ニュースAPIフィードの照会および民間伝承とのマッチング分析中..."
        ),
        "news_cached" to mapOf(
            "English" to "Using cached news database.",
            "Russian" to "Офлайн-режим новостей. Загружен архив вчерашних твитов.",
            "Spanish" to "Usando base de datos de noticias en caché.",
            "Chinese" to "使用本地新闻缓存数据库。",
            "Hindi" to "कैश की गई समाचार डेटाबेस का उपयोग कर रहे हैं।",
            "Arabic" to "استخدام قاعدة بيانات الأخبار المخزنة مؤقتاً.",
            "French" to "Utilisation de la base de données d'actualités en cache.",
            "Portuguese" to "Usando banco de dados de notícias em cache.",
            "German" to "Verwende zwischengespeicherte News-Datenbank.",
            "Japanese" to "キャッシュされたニュースデータベースを使用しています。"
        ),
        "no_posts" to mapOf(
            "English" to "No posts found",
            "Russian" to "Ничего не найдено",
            "Spanish" to "No se encontraron publicaciones",
            "Chinese" to "未找到相关发布",
            "Hindi" to "कोई पोस्ट नहीं मिली",
            "Arabic" to "لم يتم العثور على منشورات",
            "French" to "Aucun post trouvé",
            "Portuguese" to "Nenhum post encontrado",
            "German" to "Keine Beiträge gefunden",
            "Japanese" to "投稿が見つかりません"
        ),
        "folk_context" to mapOf(
            "English" to "Folk Wisdom context added",
            "Russian" to "Добавлен фольклорный контекст",
            "Spanish" to "Contexto de sabiduría popular añadido",
            "Chinese" to "已添加民间智慧背景",
            "Hindi" to "लोक ज्ञान संदर्भ जोड़ा गया",
            "Arabic" to "تم إضافة سياق الحكمة الشعبية",
            "French" to "Sagesse populaire ajoutée",
            "Portuguese" to "Contexto de sabedoria popular adicionado",
            "German" to "Volksweisheit-Kontext hinzugefügt",
            "Japanese" to "民間伝承の知恵の背景を追加"
        ),
        "folk_joke_commentary" to mapOf(
            "English" to "Folk Joke Commentary",
            "Russian" to "Фольклорный комментарий к новости",
            "Spanish" to "Comentario de chiste folclórico",
            "Chinese" to "民间幽默点评",
            "Hindi" to "लोक कथा टिप्पणी",
            "Arabic" to "تعليق النكتة الشعبية",
            "French" to "Commentaire de blague populaire",
            "Portuguese" to "Comentário de piada folclórica",
            "German" to "Volkswitz-Kommentar",
            "Japanese" to "民間伝承ユーモアの解説"
        ),
        "streaming_engine" to mapOf(
            "English" to "STREAMING ENGINE",
            "Russian" to "ДВИЖОК ВЕЩАНИЯ",
            "Spanish" to "MOTOR DE STREAMING",
            "Chinese" to "广播串流引擎",
            "Hindi" to "स्ट्रीमिंग इंजन",
            "Arabic" to "محرك البث",
            "French" to "MOTEUR DE STREAMING",
            "Portuguese" to "MOTOR DE STREAMING",
            "German" to "STREAMING-ENGINE",
            "Japanese" to "ストリーミングエンジン"
        ),
        "html5_player_desc" to mapOf(
            "English" to "HTML5 Audio Web Streamer",
            "Russian" to "HTML5 Audio Web-стример",
            "Spanish" to "Transmisor web de audio HTML5",
            "Chinese" to "HTML5音频网络播放器",
            "Hindi" to "HTML5 ऑडियो वेब स्ट्रीमर",
            "Arabic" to "مشغل ويب صوتي HTML5",
            "French" to "Web streamer audio HTML5",
            "Portuguese" to "Web streamer de áudio HTML5",
            "German" to "HTML5-Audio-Webstreamer",
            "Japanese" to "HTML5オーディオウェブストリーマー"
        ),
        "native_player_desc" to mapOf(
            "English" to "Native Android MediaPlayer",
            "Russian" to "Нативный плеер Android",
            "Spanish" to "Reproductor de Android nativo",
            "Chinese" to "Android原生播放器",
            "Hindi" to "देशी एंड्रॉइड मीडिया प्लेयर",
            "Arabic" to "مشغل أندرويد الافتراضي",
            "French" to "Lecteur Android natif",
            "Portuguese" to "Reprodutor nativo do Android",
            "German" to "Nativer Android MediaPlayer",
            "Japanese" to "Androidネイティブメディアプレーヤー"
        ),
        "html5_player_long" to mapOf(
            "English" to "Delegates audio to web interface with HTML5 <audio> tag. Supports background streaming.",
            "Russian" to "Делегирует аудио веб-интерфейсу с HTML5 тегом <audio>. Поддерживает фоновый стриминг.",
            "Spanish" to "Delega el audio a la interfaz web con la etiqueta HTML5 <audio>. Soporta transmisión en segundo plano.",
            "Chinese" to "委托给带HTML5 <audio>标签的网页界面，支持后台流媒体播放。",
            "Hindi" to "HTML5 <audio> टैग के साथ वेब इंटरफेस को ऑडियो सौंपता है। पृष्ठभूमि स्ट्रीमिंग का समर्थन करता है।",
            "Arabic" to "يفوض الصوت إلى واجهة الويب باستخدام علامة HTML5 <audio>. يدعم البث في الخلفية.",
            "French" to "Délègue l'audio à l'interface web avec la balise HTML5 <audio>. Supporte le streaming en arrière-plan.",
            "Portuguese" to "Delega o áudio para interface web com a tag HTML5 <audio>. Suporta streaming em segundo plano.",
            "German" to "Delegiert Audio an die Weboberfläche mit HTML5-<audio>-Tag. Unterstützt Hintergrund-Streaming.",
            "Japanese" to "HTML5 <audio> タグを使用したウェブインターフェースにオーディオを委任します。バックグラウンド再生対応。"
        ),
        "native_player_long" to mapOf(
            "English" to "Uses system Android media codecs for stable native playback.",
            "Russian" to "Использует системные медиакодеки Android для стабильного воспроизведения.",
            "Spanish" to "Usa códecs de medios nativos del sistema Android para reproducción estable.",
            "Chinese" to "使用Android系统媒体解码器进行稳定原生播放。",
            "Hindi" to "स्थिर देशी प्लेबैक के लिए सिस्टम एंड्रॉइड मीडिया कोडेक्स का उपयोग करता है।",
            "Arabic" to "يستخدم برامج ترميز أندرويد الافتراضية للتشغيل المستقر.",
            "French" to "Utilise les codecs système Android pour une lecture native stable.",
            "Portuguese" to "Usa codecs de mídia nativos do sistema Android para reprodução estável.",
            "German" to "Verwendet System-Android-Medien-Codecs für stabile native Wiedergabe.",
            "Japanese" to "安定したネイティブ再生のためにAndroidシステムメディアコーデックを使用します。"
        ),
        "html5_title" to mapOf(
            "English" to "HTML5 WEB STREAMER",
            "Russian" to "ВЕБ-СТРИМЕР HTML5",
            "Spanish" to "TRANSMISOR WEB HTML5",
            "Chinese" to "HTML5网页流播放器",
            "Hindi" to "HTML5 वेब स्ट्रीमर",
            "Arabic" to "ويب ستريمر HTML5",
            "French" to "STREAMER WEB HTML5",
            "Portuguese" to "STREAMER WEB HTML5",
            "German" to "HTML5-WEBSTREAMER",
            "Japanese" to "HTML5ウェブストリーマー"
        ),
        "retro_title" to mapOf(
            "English" to "RETRO BROADCASTER",
            "Russian" to "АНАЛОГОВЫЙ СТРИМЕР",
            "Spanish" to "REPRODUCTOR RETRO",
            "Chinese" to "复古电台",
            "Hindi" to "रेट्रो ब्रॉडकाستر",
            "Arabic" to "راديو ريترو",
            "French" to "RADIODIFFUSEUR RETRO",
            "Portuguese" to "TRANSMISSÃO RETRO",
            "German" to "RETRO-RUNDFUNK",
            "Japanese" to "レトロブロードキャスター"
        ),
        "broadcaster_off" to mapOf(
            "English" to "Broadcaster Off",
            "Russian" to "Поток выключен",
            "Spanish" to "Transmisor apagado",
            "Chinese" to "电台已关闭",
            "Hindi" to "ब्रॉडकास्टर बंद",
            "Arabic" to "البث مغلق",
            "French" to "Diffuseur éteint",
            "Portuguese" to "Transmissão desligada",
            "German" to "Sender aus",
            "Japanese" to "放送オフ"
        ),
        "html5_active" to mapOf(
            "English" to "HTML5 Audio Tag Active",
            "Russian" to "HTML5 Поток активен",
            "Spanish" to "Canal de audio HTML5 activo",
            "Chinese" to "HTML5音频流活动",
            "Hindi" to "HTML5 ऑडियो टैग सक्रिय",
            "Arabic" to "تدفق HTML5 نشط",
            "French" to "Flux audio HTML5 actif",
            "Portuguese" to "Transmissão HTML5 ativa",
            "German" to "HTML5-Audio-Tag aktiv",
            "Japanese" to "HTML5オーディオタグ有効"
        ),
        "live_streaming" to mapOf(
            "English" to "Live Streaming (128 kbps)",
            "Russian" to "Прямой эфир (128 kbps)",
            "Spanish" to "Transmisión en vivo (128 kbps)",
            "Chinese" to "实时电台直播 (128 kbps)",
            "Hindi" to "लाइव स्ट्रीमिंग (128 kbps)",
            "Arabic" to "بث مباشر (128 kbps)",
            "French" to "Streaming en direct (128 kbps)",
            "Portuguese" to "Transmissão ao vivo (128 kbps)",
            "German" to "Live-Streaming (128 kbps)",
            "Japanese" to "ライブ配信中 (128 kbps)"
        ),
        "tuning" to mapOf(
            "English" to "TUNING...",
            "Russian" to "ПОДКЛЮЧЕНИЕ...",
            "Spanish" to "SINTONIZANDO...",
            "Chinese" to "调谐中...",
            "Hindi" to "ट्यूनिंग...",
            "Arabic" to "جاري الضبط...",
            "French" to "RECHERCHE...",
            "Portuguese" to "SINTONIZANDO...",
            "German" to "SUCHE...",
            "Japanese" to "同調中..."
        ),
        "standby" to mapOf(
            "English" to "Standby",
            "Russian" to "Ожидание",
            "Spanish" to "En espera",
            "Chinese" to "待机",
            "Hindi" to "स्टैंडबाय",
            "Arabic" to "الاستعداد",
            "French" to "En attente",
            "Portuguese" to "Em espera",
            "German" to "Standby",
            "Japanese" to "スタンバイ"
        ),
        "add_custom_station" to mapOf(
            "English" to "Add Custom Radio Station",
            "Russian" to "Добавить свою радиостанцию",
            "Spanish" to "Añadir estación de radio personalizada",
            "Chinese" to "添加自定义电台",
            "Hindi" to "अपनी खुद की радио स्टेशन जोड़ें",
            "Arabic" to "إضافة محطة راديو مخصصة",
            "French" to "Ajouter une station de radio personnalisée",
            "Portuguese" to "Adicionar estação de rádio personalizada",
            "German" to "Eigenen Radiosender hinzufügen",
            "Japanese" to "カスタムラジオ局を追加する"
        ),
        "station_name" to mapOf(
            "English" to "Station Name",
            "Russian" to "Название станции",
            "Spanish" to "Nombre de la estación",
            "Chinese" to "电台名称",
            "Hindi" to "स्टेशन का नाम",
            "Arabic" to "اسم المحطة",
            "French" to "Nom de la station",
            "Portuguese" to "Nome da estação",
            "German" to "Sendername",
            "Japanese" to "放送局名"
        ),
        "stream_url" to mapOf(
            "English" to "Stream URL (http/https)",
            "Russian" to "URL-адрес потока (http/https)",
            "Spanish" to "URL de transmisión (http/https)",
            "Chinese" to "串流URL (http/https)",
            "Hindi" to "स्ट्रीम URL (http/https)",
            "Arabic" to "رابط البث (http/https)",
            "French" to "URL du flux (http/https)",
            "Portuguese" to "URL da transmissão (http/https)",
            "German" to "Stream-URL (http/https)",
            "Japanese" to "配信URL (http/https)"
        ),
        "select_genre" to mapOf(
            "English" to "Select Style / Genre",
            "Russian" to "Выберите стиль / жанр",
            "Spanish" to "Seleccionar estilo / género",
            "Chinese" to "选择风格/流派",
            "Hindi" to "शैली / जॉनर चुनें",
            "Arabic" to "اختر الأسلوب / النوع",
            "French" to "Sélectionner style / genre",
            "Portuguese" to "Selecionar estilo / gênero",
            "German" to "Stil / Genre auswählen",
            "Japanese" to "スタイル / ジャンルを選択"
        ),
        "add_station_btn" to mapOf(
            "English" to "Add Station",
            "Russian" to "Добавить станцию",
            "Spanish" to "Añadir estación",
            "Chinese" to "添加电台",
            "Hindi" to "स्टेशन जोड़ें",
            "Arabic" to "إضافة المحطة",
            "French" to "Ajouter la station",
            "Portuguese" to "Adicionar estação",
            "German" to "Sender hinzufügen",
            "Japanese" to "局を追加"
        ),
        "station_added" to mapOf(
            "English" to "Station added!",
            "Russian" to "Станция добавлена!",
            "Spanish" to "¡Estación añadida!",
            "Chinese" to "电台已添加!",
            "Hindi" to "स्टेशन जोड़ा गया!",
            "Arabic" to "تم إضافة المحطة!",
            "French" to "Station ajoutée !",
            "Portuguese" to "Estação adicionada!",
            "German" to "Sender hinzugefügt!",
            "Japanese" to "放送局を追加しました！"
        ),
        "invalid_url" to mapOf(
            "English" to "Invalid URL!",
            "Russian" to "Неверный URL!",
            "Spanish" to "¡URL inválido!",
            "Chinese" to "无效的URL!",
            "Hindi" to "अमान्य URL!",
            "Arabic" to "رابط غير صالح!",
            "French" to "URL invalide !",
            "Portuguese" to "URL inválido!",
            "German" to "Ungültige URL!",
            "Japanese" to "URLが無効です！"
        ),
        "fill_all_fields" to mapOf(
            "English" to "Please fill all fields!",
            "Russian" to "Пожалуйста, заполните все поля!",
            "Spanish" to "¡Por favor complete todos los campos!",
            "Chinese" to "请填写所有字段!",
            "Hindi" to "कृपया सभी फ़ील्ड भरें!",
            "Arabic" to "يرجى ملء جميع الحقول!",
            "French" to "Veuillez remplir tous les champs !",
            "Portuguese" to "Por favor preencha todos os campos!",
            "German" to "Bitte alle Felder ausfüllen!",
            "Japanese" to "すべての項目を入力してください！"
        ),
        "available_broadcasts" to mapOf(
            "English" to "Available Broadcasts:",
            "Russian" to "Доступные эфиры:",
            "Spanish" to "Emisiones disponibles:",
            "Chinese" to "可用电台:",
            "Hindi" to "उपलब्ध प्रसारण:",
            "Arabic" to "الإذاعات المتاحة:",
            "French" to "Émissions disponibles :",
            "Portuguese" to "Transmissões disponíveis:",
            "German" to "Verfügbare Sendungen:",
            "Japanese" to "利用可能な放送:"
        ),
        "fav_stations_header" to mapOf(
            "English" to "Favorite Radio Stations:",
            "Russian" to "Избранные станции:",
            "Spanish" to "Estaciones favoritas:",
            "Chinese" to "收藏电台:",
            "Hindi" to "पसंदीदा रेडियो स्टेशन:",
            "Arabic" to "محطات الراديو المفضلة:",
            "French" to "Stations de radio favorites :",
            "Portuguese" to "Estações de rádio favoritas:",
            "German" to "Favoriten-Radiosender:",
            "Japanese" to "お気に入りのラジオ局:"
        ),
        "no_fav_stations" to mapOf(
            "English" to "No favorite stations yet. Click the star on any radio stream!",
            "Russian" to "Нет избранных станций. Жмите звезду на вкладке Радио!",
            "Spanish" to "No hay estaciones favoritas aún. ¡Haz clic en la estrella de cualquier transmisión!",
            "Chinese" to "暂无收藏电台。请在“电台”标签页中点击星形按钮！",
            "Hindi" to "अभी तक कोई पसंदीदा स्टेशन नहीं है। किसी भी रेडियो स्ट्रीम पर स्टार पर क्लिक करें!",
            "Arabic" to "لا توجد محطات مفضلة بعد. انقر على النجمة في أي بث للراديو!",
            "French" to "Pas encore de station favorite. Cliquez sur l'étoile sur un flux radio !",
            "Portuguese" to "Nenhuma estação favorita ainda. Clique na estrela em qualquer transmissão!",
            "German" to "Noch keine Favoriten-Sender. Klicke auf das Sternchen bei einem Radiosender!",
            "Japanese" to "お気に入りのラジオ局はありません。ラジオ配信の星マークをタップして追加してください！"
        ),
        "saved_anecdotes_header" to mapOf(
            "English" to "Saved Folk Anecdotes:",
            "Russian" to "Любимые анекдоты:",
            "Spanish" to "Anécdotas populares guardadas:",
            "Chinese" to "已保存民间幽默:",
            "Hindi" to "सहेजे गए लोक चुटकुले:",
            "Arabic" to "النكات الشعبية المحفوظة:",
            "French" to "Blagues populaires enregistrées :",
            "Portuguese" to "Anedotas folclóricas salvas:",
            "German" to "Gespeicherte Volksanekdoten:",
            "Japanese" to "保存済みの民間伝承ユーモア:"
        ),
        "no_saved_anecdotes" to mapOf(
            "English" to "No saved anecdotes yet. Go generate some stories with our storyteller!",
            "Russian" to "Нет сохранённых анекдотов. Загляните к Сказочнику!",
            "Spanish" to "No hay chistes guardados aún. ¡Genera algunas historias con nuestro cuentacuentos!",
            "Chinese" to "暂无保存故事。快去故事家页面生成一些有趣的故事吧！",
            "Hindi" to "अभी तक कोई सहेजा गया चुटकुला नहीं है। हमारे कहानीकार के साथ कुछ कहानियाँ उत्पन्न करें!",
            "Arabic" to "لا توجد نكات محفوظة بعد. اذهب لتوليد بعض القصص مع حكواتينا!",
            "French" to "Pas encore de blague enregistrée. Allez générer des histoires avec notre conteur !",
            "Portuguese" to "Nenhuma anedota salva ainda. Vá gerar algumas histórias com o nosso contador de histórias!",
            "German" to "Noch keine gespeicherten Anekdoten. Lass dir vom Geschichtenerzähler welche erzählen!",
            "Japanese" to "保存済みのユーモアはありません。「語り部」でお話を作成してみましょう！"
        ),
        "style_prefix" to mapOf(
            "English" to "Style:",
            "Russian" to "Стиль:",
            "Spanish" to "Estilo:",
            "Chinese" to "风格:",
            "Hindi" to "शैली:",
            "Arabic" to "الأسلوب:",
            "French" to "Style :",
            "Portuguese" to "Estilo:",
            "German" to "Stil:",
            "Japanese" to "スタイル:"
        ),
        "topic_prefix" to mapOf(
            "English" to "Topic:",
            "Russian" to "Тема:",
            "Spanish" to "Tema:",
            "Chinese" to "主题:",
            "Hindi" to "विषय:",
            "Arabic" to "الموضوع:",
            "French" to "Sujet :",
            "Portuguese" to "Tema:",
            "German" to "Thema:",
            "Japanese" to "トピック:"
        ),
        "copied" to mapOf(
            "English" to "Copied!",
            "Russian" to "Скопировано!",
            "Spanish" to "¡Copiado!",
            "Chinese" to "已复制!",
            "Hindi" to "कॉपी हो गया!",
            "Arabic" to "تم النسخ!",
            "French" to "Copié !",
            "Portuguese" to "Copiado!",
            "German" to "Kopiert!",
            "Japanese" to "コピーしました！"
        ),
        "select_bg_style" to mapOf(
            "English" to "Select Design Style",
            "Russian" to "Выберите стиль оформления",
            "Spanish" to "Seleccionar estilo de diseño",
            "Chinese" to "选择设计主题风格",
            "Hindi" to "डिज़ाइन शैली चुनें",
            "Arabic" to "اختر أسلوب التصميم",
            "French" to "Sélectionner le style de design",
            "Portuguese" to "Selecionar estilo de design",
            "German" to "Design-Stil auswählen",
            "Japanese" to "デザインスタイルを選択"
        ),
        "done" to mapOf(
            "English" to "Done",
            "Russian" to "Готово",
            "Spanish" to "Listo",
            "Chinese" to "完成",
            "Hindi" to "हो गया",
            "Arabic" to "تم",
            "French" to "Terminé",
            "Portuguese" to "Concluído",
            "German" to "Fertig",
            "Japanese" to "完了"
        ),
        "loading_replies" to mapOf(
            "English" to "Loading tweet replies...",
            "Russian" to "Показ ответов к публикации...",
            "Spanish" to "Cargando respuestas del tweet...",
            "Chinese" to "正在加载推文回复...",
            "Hindi" to "ट्वीट उत्तर लोड हो रहे हैं...",
            "Arabic" to "جاري تحميل ردود التغريدة...",
            "French" to "Chargement des réponses...",
            "Portuguese" to "Carregando respostas do tweet...",
            "German" to "Lade Tweet-Antworten...",
            "Japanese" to "返信を読み込んでいます..."
        ),
        "storyteller_quote" to mapOf(
            "English" to "“I have 20 folk jokes for any situation!”",
            "Russian" to "«У меня на любую ситуацию найдётся 20 анекдотов!»",
            "Spanish" to "«¡Tengo 20 chistes populares para cualquier situación!»",
            "Chinese" to "“对于任何情况，我都有20个民间笑话！”",
            "Hindi" to "“मेरे पास किसी भी स्थिति के लिए 20 लोक चुटकुले हैं!”",
            "Arabic" to "«عندي 20 نكتة شعبية لأي موقف!»",
            "French" to "« J'ai 20 blagues populaires pour chaque situation ! »",
            "Portuguese" to "«Tenho 20 piadas populares para qualquer situação!»",
            "German" to "„Ich habe für jede Situation 20 Volkswitze!“",
            "Japanese" to "「どんな状況でも、私には20の民間伝承ユーモアがあります！」"
        ),
        "select_folklore_style" to mapOf(
            "English" to "Select folklore style:",
            "Russian" to "Выберите фольклорный стиль:",
            "Spanish" to "Selecciona el estilo folclórico:",
            "Chinese" to "选择民俗风情风格:",
            "Hindi" to "लोकगीत शैली चुनें:",
            "Arabic" to "اختر الأسلوب الشعبي:",
            "French" to "Sélectionnez le style folklorique :",
            "Portuguese" to "Selecione o estilo folclórico:",
            "German" to "Volksstil auswählen:",
            "Japanese" to "フォークロアのスタイルを選択:"
        ),
        "situation_label" to mapOf(
            "English" to "Enter a situation (e.g. mother-in-law, market, space)",
            "Russian" to "Задайте любую ситуацию (например: тёща, базар, космос)",
            "Spanish" to "Ingrese una situación (por ejemplo, suegra, mercado, espacio)",
            "Chinese" to "输入情境（例如：岳母、集市、太空）",
            "Hindi" to "एक स्थिति दर्ज करें (जैसे कि सास, बाजार, अंतरिक्ष)",
            "Arabic" to "أدخل موقفاً (مثلاً: الحماة، السوق، الفضاء)",
            "French" to "Saisissez une situation (ex : belle-mère, marché, espace)",
            "Portuguese" to "Insira uma situação (por exemplo: sogra, mercado, espaço)",
            "German" to "Gib eine Situation ein (z. B. Schwiegermutter, Markt, Weltraum)",
            "Japanese" to "状況を入力してください（例：義母、バザール、宇宙）"
        ),
        "situation_placeholder" to mapOf(
            "English" to "An argument at the bazaar...",
            "Russian" to "Ссора на базаре...",
            "Spanish" to "Una discusión en el mercado...",
            "Chinese" to "集市上的争吵...",
            "Hindi" to "बाजार में एक बहस...",
            "Arabic" to "شجار في السوق...",
            "French" to "Une dispute au marché...",
            "Portuguese" to "Uma discussão no mercado...",
            "German" to "Ein Streit auf dem Markt...",
            "Japanese" to "バザールでの口論..."
        ),
        "tell_jokes_btn" to mapOf(
            "English" to "Tell Folk Jokes!",
            "Russian" to "Рассказать анекдоты!",
            "Spanish" to "¡Contar chistes populares!",
            "Chinese" to "讲民间笑话！",
            "Hindi" to "लोक चुटकुले सुनाओ!",
            "Arabic" to "اخبرنا بنكات شعبية!",
            "French" to "Raconter des blagues !",
            "Portuguese" to "Contar piadas populares!",
            "German" to "Volkswitze erzählen!",
            "Japanese" to "民間伝承ユーモアを語る！"
        ),
        "offline_notice" to mapOf(
            "English" to "Offline mode. Showing saved fallback folklore.",
            "Russian" to "Офлайн-режим. Загружены классические прибаутки.",
            "Spanish" to "Modo sin conexión. Mostrando folklore guardado de reserva.",
            "Chinese" to "离线模式。正在显示本地缓存的经典民间笑话。",
            "Hindi" to "ऑफ़लाइन मोड। सहेजे गए बैकअप लोक चुटकुले दिखा रहा है।",
            "Arabic" to "وضع التشغيل المباشر غير متوفر. عرض الفولكلور المحفوظ.",
            "French" to "Mode hors ligne. Affichage du folklore de secours.",
            "Portuguese" to "Modo offline. Mostrando folclore de backup.",
            "German" to "Offline-Modus. Zeige gespeicherte Volksanekdoten.",
            "Japanese" to "オフラインモード。保存されたバックアップユーモアを表示中。"
        ),
        "storyteller_says" to mapOf(
            "English" to "Storyteller:",
            "Russian" to "Сказочник говорит:",
            "Spanish" to "El cuentacuentos dice:",
            "Chinese" to "讲故事的人说:",
            "Hindi" to "कहानीकार कहता है:",
            "Arabic" to "الحكواتي يقول:",
            "French" to "Le conteur dit :",
            "Portuguese" to "O contador de histórias diz:",
            "German" to "Der Erzähler sagt:",
            "Japanese" to "語り部いわく:"
        ),
        "parting_words" to mapOf(
            "English" to "Parting words:",
            "Russian" to "И напоследок:",
            "Spanish" to "Palabras de despedida:",
            "Chinese" to "临别赠言:",
            "Hindi" to "विदाई शब्द:",
            "Arabic" to "كلمات الوداع:",
            "French" to "Mots d'adieu :",
            "Portuguese" to "Palavras de despedida:",
            "German" to "Abschiedsworte:",
            "Japanese" to "最後に一言:"
        ),
        "folk_saying_header" to mapOf(
            "English" to "Folk Saying",
            "Russian" to "Прикол от народа",
            "Spanish" to "Dicho popular",
            "Chinese" to "民间趣闻",
            "Hindi" to "लोक कहावत",
            "Arabic" to "مقولة شعبية",
            "French" to "Dicton populaire",
            "Portuguese" to "Ditado popular",
            "German" to "Volksweisheit",
            "Japanese" to "民俗ユーモア"
        ),
        "monitoring_x_header" to mapOf(
            "English" to "Simulated X.com Live Feed",
            "Russian" to "Мониторинг X.com (Twitter Live)",
            "Spanish" to "Feed de X.com en vivo simulado",
            "Chinese" to "模拟X.com实时推文流",
            "Hindi" to "सिम्युलेटेड X.com लाइव फ़ीड",
            "Arabic" to "بث X.com المباشر المحاكى",
            "French" to "Flux en direct X.com simulé",
            "Portuguese" to "Feed ao vivo simulado do X.com",
            "German" to "Simulierter X.com-Live-Feed",
            "Japanese" to "シミュレートされたX.comライブフィード"
        ),
        "monitoring_x_subheader" to mapOf(
            "English" to "Live-simulate the top 100 news sources on X.com via Gemini API",
            "Russian" to "Поиск по API и ИИ-анализ 100 топовых каналов",
            "Spanish" to "Simulación en vivo de las 100 principales fuentes en X.com",
            "Chinese" to "通过Gemini API对X.com上排名前100的新闻源进行实时模拟",
            "Hindi" to "जेमिनी एपीआई के माध्यम से X.com पर शीर्ष 100 समाचार स्रोतों का लाइव सिमुलेशन",
            "Arabic" to "محاكاة مباشرة لأهم 100 مصدر إخباري على X.com عبر واجهة Gemini",
            "French" to "Simulation en direct des 100 principales sources d'actualités sur X.com via Gemini API",
            "Portuguese" to "Simulação ao vivo das 100 principais fontes de notícias no X.com",
            "German" to "Live-Simulation der Top-100-Newsquellen auf X.com via Gemini-API",
            "Japanese" to "Gemini APIを介してX.comのトップ100ニュースソースをリアルタイムにシミュレート"
        ),
        "filter_all" to mapOf(
            "English" to "All Trends",
            "Russian" to "Все тренды",
            "Spanish" to "Todas las tendencias",
            "Chinese" to "所有趋势",
            "Hindi" to "सभी रुझान",
            "Arabic" to "جميع الاتجاهات",
            "French" to "Toutes les tendances",
            "Portuguese" to "Todas as tendências",
            "German" to "Alle Trends",
            "Japanese" to "すべてのトレンド"
        ),
        "filter_space" to mapOf(
            "English" to "#Space",
            "Russian" to "#Космос",
            "Spanish" to "#Espacio",
            "Chinese" to "#太空",
            "Hindi" to "#अंतरिक्ष",
            "Arabic" to "#الفضاء",
            "French" to "#Espace",
            "Portuguese" to "#Espaço",
            "German" to "#Weltraum",
            "Japanese" to "#宇宙"
        ),
        "filter_tech" to mapOf(
            "English" to "#Tech",
            "Russian" to "#Технологии",
            "Spanish" to "#Tecnología",
            "Chinese" to "#科技",
            "Hindi" to "#तकनीकी",
            "Arabic" to "#التقنية",
            "French" to "#Tech",
            "Portuguese" to "#Tecnologia",
            "German" to "#Tech",
            "Japanese" to "#テクノロジー"
        ),
        "filter_culture" to mapOf(
            "English" to "#Culture",
            "Russian" to "#Культура",
            "Spanish" to "#Cultura",
            "Chinese" to "#文化",
            "Hindi" to "#संस्कृति",
            "Arabic" to "#الثقافة",
            "French" to "#Culture",
            "Portuguese" to "#Cultura",
            "German" to "#Kultur",
            "Japanese" to "#文化"
        ),
        "filter_society" to mapOf(
            "English" to "#Society",
            "Russian" to "#Общество",
            "Spanish" to "#Sociedad",
            "Chinese" to "#社会",
            "Hindi" to "#समाज",
            "Arabic" to "#المجتمع",
            "French" to "#Société",
            "Portuguese" to "#Sociedade",
            "German" to "#Gesellschaft",
            "Japanese" to "#社会"
        ),
        "folk_context_header" to mapOf(
            "English" to "Folk Wisdom context added",
            "Russian" to "Добавлен фольклорный контекст",
            "Spanish" to "Contexto de sabiduría popular añadido",
            "Chinese" to "已添加民间智慧背景",
            "Hindi" to "लोक ज्ञान संदर्भ जोड़ा गया",
            "Arabic" to "تم إضافة سياق الحكمة الشعبية",
            "French" to "Sagesse populaire ajoutée",
            "Portuguese" to "Contexto de sabedoria popular adicionado",
            "German" to "Volksweisheit-Kontext hinzugefügt",
            "Japanese" to "民間伝承の知恵の背景を追加"
        ),
        "radio_stream" to mapOf(
            "English" to "Radio Stream",
            "Russian" to "Радио поток",
            "Spanish" to "Transmisión de radio",
            "Chinese" to "广播电台",
            "Hindi" to "रेдио स्ट्रीम",
            "Arabic" to "بث الراديو",
            "French" to "Flux radio",
            "Portuguese" to "Transmissão de rádio",
            "German" to "Radio-Stream",
            "Japanese" to "ラジオストリーム"
        ),
        "playing_live" to mapOf(
            "English" to "Playing live...",
            "Russian" to "Прямой эфир...",
            "Spanish" to "Reproduciendo en vivo...",
            "Chinese" to "正在直播播放...",
            "Hindi" to "लाइव चल रहा है...",
            "Arabic" to "بث مباشر...",
            "French" to "Lecture en direct...",
            "Portuguese" to "Reproduzindo ao vivo...",
            "German" to "Live abspielen...",
            "Japanese" to "ライブ再生中..."
        ),
        "tuning_in" to mapOf(
            "English" to "Tuning in...",
            "Russian" to "Подключение к эфиру...",
            "Spanish" to "Sintonizando...",
            "Chinese" to "正在调谐...",
            "Hindi" to "ट्यूनिंग कर रहे हैं...",
            "Arabic" to "جاري الضبط...",
            "French" to "Sintonisation...",
            "Portuguese" to "Sintonizando...",
            "German" to "Abstimmen...",
            "Japanese" to "同調中..."
        ),
        "on_pause" to mapOf(
            "English" to "Paused",
            "Russian" to "На паузе",
            "Spanish" to "Pausado",
            "Chinese" to "已暂停",
            "Hindi" to "विराम दिया गया",
            "Arabic" to "مؤقت",
            "French" to "En pause",
            "Portuguese" to "Pausado",
            "German" to "Pausiert",
            "Japanese" to "一時停止"
        ),
        "live_badge" to mapOf(
            "English" to "LIVE",
            "Russian" to "В ЭФИРЕ",
            "Spanish" to "EN VIVO",
            "Chinese" to "直播",
            "Hindi" to "लाइव",
            "Arabic" to "مباشر",
            "French" to "EN DIRECT",
            "Portuguese" to "AO VIVO",
            "German" to "LIVE",
            "Japanese" to "ライブ"
        ),
        "live_stream" to mapOf(
            "English" to "Live Radio Stream",
            "Russian" to "Прямой радиоэфир",
            "Spanish" to "Transmisión de radio en vivo",
            "Chinese" to "实时广播流",
            "Hindi" to "लाइव रेडियो स्ट्रीम",
            "Arabic" to "بث إذاعي مباشر",
            "French" to "Flux radio en direct",
            "Portuguese" to "Transmissão de rádio ao vivo",
            "German" to "Live-Radiostream",
            "Japanese" to "ライブラジオストリーム"
        ),
        "seek_to_live" to mapOf(
            "English" to "Jump to Live",
            "Russian" to "Перейти к эфиру",
            "Spanish" to "Ir a en vivo",
            "Chinese" to "跳至直播",
            "Hindi" to "लाइव पर जाएं",
            "Arabic" to "الانتقال إلى المباشر",
            "French" to "Aller au direct",
            "Portuguese" to "Ir para o ao vivo",
            "German" to "Zum Live-Stream",
            "Japanese" to "ライブへジャンプ"
        ),
        "ready_to_play" to mapOf(
            "English" to "Tap play to listen",
            "Russian" to "Нажмите для прослушивания",
            "Spanish" to "Toca para escuchar",
            "Chinese" to "点击播放收听",
            "Hindi" to "सुनने के लिए टैप करें",
            "Arabic" to "اضغط للاستماع",
            "French" to "Appuyez pour écouter",
            "Portuguese" to "Toque para ouvir",
            "German" to "Tippen zum Anhören",
            "Japanese" to "タップして再生"
        ),
        "volume_title" to mapOf(
            "English" to "Volume",
            "Russian" to "Громкость",
            "Spanish" to "Volumen",
            "Chinese" to "音量",
            "Hindi" to "आवाज़",
            "Arabic" to "مستوى الصوت",
            "French" to "Volume",
            "Portuguese" to "Volume",
            "German" to "Lautstärke",
            "Japanese" to "音量"
        ),
        "prev_station" to mapOf(
            "English" to "Previous Station",
            "Russian" to "Предыдущая станция",
            "Spanish" to "Estación anterior",
            "Chinese" to "上一电台",
            "Hindi" to "पिछला स्टेशन",
            "Arabic" to "المحطة السابقة",
            "French" to "Station précédente",
            "Portuguese" to "Estação anterior",
            "German" to "Vorheriger Sender",
            "Japanese" to "前のステーション"
        ),
        "next_station" to mapOf(
            "English" to "Next Station",
            "Russian" to "Следующая станция",
            "Spanish" to "Siguiente estación",
            "Chinese" to "下一电台",
            "Hindi" to "अगला स्टेशन",
            "Arabic" to "المحطة التالية",
            "French" to "Station suivante",
            "Portuguese" to "Próxima estação",
            "German" to "Nächster Sender",
            "Japanese" to "次のステーション"
        ),
        "audio_quality" to mapOf(
            "English" to "128 kbps • Live Stereo",
            "Russian" to "128 кбит/с • Стерео эфир",
            "Spanish" to "128 kbps • Estéreo en vivo",
            "Chinese" to "128 kbps • 实时立体声",
            "Hindi" to "128 kbps • लाइव स्टीरियो",
            "Arabic" to "128 كيلوبت/ث • ستريو مباشر",
            "French" to "128 kbps • Stéréo direct",
            "Portuguese" to "128 kbps • Estéreo ao vivo",
            "German" to "128 kbit/s • Live-Stereo",
            "Japanese" to "128 kbps • ライブステレオ"
        ),
        "drag_to_seek" to mapOf(
            "English" to "Live buffer seek",
            "Russian" to "Перемотка буфера",
            "Spanish" to "Buscar en búfer",
            "Chinese" to "拖动快进/快退",
            "Hindi" to "बफ़र सीक",
            "Arabic" to "شريط التقديم",
            "French" to "Déplacement mémoire tampon",
            "Portuguese" to "Avançar/Retroceder",
            "German" to "Puffer-Suchlauf",
            "Japanese" to "バッファシーク"
        )
    )

    fun get(key: String, lang: String): String {
        val entry = translations[key] ?: return key
        return entry[lang] ?: entry["English"] ?: key
    }

    fun getCultureName(culture: String, lang: String): String {
        val cultures = mapOf(
            "uzbek" to mapOf(
                "English" to "Uzbek 🇺🇿", "Russian" to "Узбекский 🇺🇿", "Spanish" to "Uzbeko 🇺🇿",
                "Chinese" to "乌兹别克 🇺🇿", "Hindi" to "उज़्बेक 🇺🇿", "Arabic" to "الأوزبكي 🇺🇿",
                "French" to "Ouzbek 🇺🇿", "Portuguese" to "Uzbeque 🇺🇿", "German" to "Usbekisch 🇺🇿", "Japanese" to "ウズベク 🇺🇿"
            ),
            "armenian" to mapOf(
                "English" to "Armenian 🇦🇲", "Russian" to "Армянский 🇦🇲", "Spanish" to "Armenio 🇦🇲",
                "Chinese" to "亚美尼亚 🇦🇲", "Hindi" to "आर्मेनियाई 🇦🇲", "Arabic" to "الأرمني 🇦🇲",
                "French" to "Arménien 🇦🇲", "Portuguese" to "Armênio 🇦🇲", "German" to "Armenisch 🇦🇲", "Japanese" to "アルメニア 🇦🇲"
            ),
            "jewish" to mapOf(
                "English" to "Jewish ✡️", "Russian" to "Еврейский ✡️", "Spanish" to "Judío ✡️",
                "Chinese" to "犹太人 ✡️", "Hindi" to "यहूदी ✡️", "Arabic" to "اليهودي ✡️",
                "French" to "Juif ✡️", "Portuguese" to "Judeu ✡️", "German" to "Jüdisch ✡️", "Japanese" to "ユダヤ ✡️"
            ),
            "chukchi" to mapOf(
                "English" to "Chukchi 🏹", "Russian" to "Чукотский 🏹", "Spanish" to "Chukchi 🏹",
                "Chinese" to "楚科奇 🏹", "Hindi" to "चुकची 🏹", "Arabic" to "تشوكشي 🏹",
                "French" to "Chouktche 🏹", "Portuguese" to "Chukchi 🏹", "German" to "Tschuktschen 🏹", "Japanese" to "チュクチ 🏹"
            ),
            "yakut" to mapOf(
                "English" to "Yakut ❄️", "Russian" to "Якутский ❄️", "Spanish" to "Yakuto ❄️",
                "Chinese" to "雅库特 ❄️", "Hindi" to "याकुत ❄️", "Arabic" to "ياكوت ❄️",
                "French" to "Iakoute ❄️", "Portuguese" to "Yakut ❄️", "German" to "Jakutisch ❄️", "Japanese" to "ヤクート ❄️"
            ),
            "kamchatka" to mapOf(
                "English" to "Kamchatka 🌋", "Russian" to "Камчатский 🌋", "Spanish" to "Kamchatka 🌋",
                "Chinese" to "堪察加 🌋", "Hindi" to "कमचटका 🌋", "Arabic" to "كامتشاتكا 🌋",
                "French" to "Kamchatka 🌋", "Portuguese" to "Kamchatka 🌋", "German" to "Kamtschatka 🌋", "Japanese" to "カムチャツカ 🌋"
            ),
            "american" to mapOf(
                "English" to "American 🤠", "Russian" to "Американский 🤠", "Spanish" to "Americano 🤠",
                "Chinese" to "美式牛仔 🤠", "Hindi" to "अमेरिकी 🤠", "Arabic" to "الأمريكي 🤠",
                "French" to "Américain 🤠", "Portuguese" to "Americano 🤠", "German" to "Amerikanisch 🤠", "Japanese" to "アメリカン 🤠"
            ),
            "african" to mapOf(
                "English" to "African 🦁", "Russian" to "Африканский 🦁", "Spanish" to "Africano 🦁",
                "Chinese" to "非洲 🦁", "Hindi" to "अफ्रीकी 🦁", "Arabic" to "الأفريقي 🦁",
                "French" to "Africain 🦁", "Portuguese" to "Africano 🦁", "German" to "Afrikanisch 🦁", "Japanese" to "アフリカ 🦁"
            )
        )

        val item = cultures[culture.lowercase()] ?: return culture
        return item[lang] ?: item["English"] ?: culture
    }

    fun getDesignStyleDisplayName(style: String, lang: String): String {
        val names = mapOf(
            "Registan" to mapOf(
                "English" to "Registan 🕌", "Russian" to "Регистан 🕌", "Spanish" to "Registán 🕌",
                "Chinese" to "雷吉斯坦 🕌", "Hindi" to "रेगिस्तान 🕌", "Arabic" to "ريجستان 🕌",
                "French" to "Régistan 🕌", "Portuguese" to "Registan 🕌", "German" to "Registan 🕌", "Japanese" to "レギスタン 🕌"
            ),
            "Giza" to mapOf(
                "English" to "Giza 📐", "Russian" to "Гиза 📐", "Spanish" to "Guiza 📐",
                "Chinese" to "吉萨 📐", "Hindi" to "गीज़ा 📐", "Arabic" to "الجيزة 📐",
                "French" to "Gizeh 📐", "Portuguese" to "Giza 📐", "German" to "Gizeh 📐", "Japanese" to "ギザ 📐"
            ),
            "India" to mapOf(
                "English" to "India 🦚", "Russian" to "Индия 🦚", "Spanish" to "India 🦚",
                "Chinese" to "印度 🦚", "Hindi" to "भारत 🦚", "Arabic" to "الهند 🦚",
                "French" to "Inde 🦚", "Portuguese" to "Índia 🦚", "German" to "Indien 🦚", "Japanese" to "インド 🦚"
            ),
            "Peru" to mapOf(
                "English" to "Peru 🦙", "Russian" to "Перу 🦙", "Spanish" to "Perú 🦙",
                "Chinese" to "秘鲁 🦙", "Hindi" to "पेरू 🦙", "Arabic" to "بيرو 🦙",
                "French" to "Pérou 🦙", "Portuguese" to "Peru 🦙", "German" to "Peru 🦙", "Japanese" to "ペルー 🦙"
            ),
            "Gaudi" to mapOf(
                "English" to "Gaudi 🦎", "Russian" to "Гауди 🦎", "Spanish" to "Gaudí 🦎",
                "Chinese" to "高迪 🦎", "Hindi" to "गौडी 🦎", "Arabic" to "غودي 🦎",
                "French" to "Gaudi 🦎", "Portuguese" to "Gaudi 🦎", "German" to "Gaudi 🦎", "Japanese" to "ガウディ 🦎"
            ),
            "VanGogh" to mapOf(
                "English" to "Van Gogh 🌻", "Russian" to "Ван Гог 🌻", "Spanish" to "Van Gogh 🌻",
                "Chinese" to "凡高 🌻", "Hindi" to "वान गाग 🌻", "Arabic" to "فان جوخ 🌻",
                "French" to "Van Gogh 🌻", "Portuguese" to "Van Gogh 🌻", "German" to "Van Gogh 🌻", "Japanese" to "ゴッホ 🌻"
            ),
            "AvantGarde" to mapOf(
                "English" to "Avant-garde 🔴", "Russian" to "Авангард 🔴", "Spanish" to "Vanguardia 🔴",
                "Chinese" to "先锋派 🔴", "Hindi" to "अवांट-गार्डे 🔴", "Arabic" to "الطليعية 🔴",
                "French" to "Avant-garde 🔴", "Portuguese" to "Vanguarda 🔴", "German" to "Avantgarde 🔴", "Japanese" to "アヴァンギャルド 🔴"
            )
        )
        val item = names[style] ?: return style
        return item[lang] ?: item["English"] ?: style
    }

    fun getDesignStyleName(style: String, lang: String): String {
        val styles = mapOf(
            "Registan" to mapOf(
                "English" to "Eastern mosaic and turquoise tiles",
                "Russian" to "Рельефная мозаика Востока",
                "Spanish" to "Mosaico oriental y azulejos turquesas",
                "Chinese" to "东方马赛克与绿松石花纹",
                "Hindi" to "पूर्वी मोज़ेक और फ़िरोज़ा टाइलें",
                "Arabic" to "الفسيفساء الشرقية والبلاط الفيروزي",
                "French" to "Mosaïque orientale et carreaux turquoise",
                "Portuguese" to "Mosaico oriental e azulejos turquesa",
                "German" to "Östliches Mosaik und türkisfarbene Fliesen",
                "Japanese" to "東洋のモザイクとターコイズブルーのタイル"
            ),
            "Giza" to mapOf(
                "English" to "Sahara sands, pyramids and golden sun",
                "Russian" to "Пески Сахары, пирамиды и золотое солнце",
                "Spanish" to "Arenas del Sahara, pirámides y sol dorado",
                "Chinese" to "撒哈拉沙漠、金字塔与金色太阳",
                "Hindi" to "सहारा रेत, पिरामिड और सुनहरा सूरज",
                "Arabic" to "رمال الصحراء والأهرامات والشمس الذهبية",
                "French" to "Sables du Sahara, pyramides et soleil d'or",
                "Portuguese" to "Areias do Saara, pirâmides e sol dourado",
                "German" to "Saharasand, Pyramiden und goldene Sonne",
                "Japanese" to "サハラ砂漠、ピラミッドと黄金の太陽"
            ),
            "India" to mapOf(
                "English" to "Saffron colors, spices and mandala patterns",
                "Russian" to "Цвета шафрана, специй и узоры мандалы",
                "Spanish" to "Colores de azafrán, especias y patrones de mandala",
                "Chinese" to "藏红花色、香料与曼陀罗图案",
                "Hindi" to "केसरिया रंग, मसाले और मंडला पैटर्न",
                "Arabic" to "ألوان الزعفران والتوابل وأنماط الماندالا",
                "French" to "Couleurs safran, épices et motifs mandala",
                "Portuguese" to "Cores de açafrão, especiarias e padrões de mandala",
                "German" to "Safranfarben, Gewürze und Mandala-Muster",
                "Japanese" to "サフラン色、スパイスとマンダラ模様"
            ),
            "Peru" to mapOf(
                "English" to "Incan geometric ornaments and poncho stripes",
                "Russian" to "Геометрические орнаменты инков и пончо",
                "Spanish" to "Ornamentos geométricos incas y rayas de poncho",
                "Chinese" to "印加几何装饰与披风条纹",
                "Hindi" to "इंका ज्यामितीय गहने और पोंचो धारियां",
                "Arabic" to "الحلي الهندسية للإنكا وخطوط البونشو",
                "French" to "Ornements géométriques incas et rayures poncho",
                "Portuguese" to "Ornamentos geométricos incas e listras de poncho",
                "German" to "Geometrische Inka-Ornamente und Poncho-Streifen",
                "Japanese" to "インカの幾何学模様とポンチョ柄"
            ),
            "Gaudi" to mapOf(
                "English" to "Organic waves and Trencadís mosaic",
                "Russian" to "Органические волны и мозаика Тренкадис",
                "Spanish" to "Olas orgánicas y mosaico Trencadís",
                "Chinese" to "有机曲线与高迪碎瓷拼贴",
                "Hindi" to "जैविक तरंगें और ट्रेंकाडिस मोज़ेक",
                "Arabic" to "الأمواج العضوية وفسيفساء ترينكاديس",
                "French" to "Vagues organiques et mosaïque trencadís",
                "Portuguese" to "Ondas orgânicas e mosaico Trencadís",
                "German" to "Organische Wellen und Trencadís-Mosaik",
                "Japanese" to "有機的な波とトレンカディスモザイク"
            ),
            "VanGogh" to mapOf(
                "English" to "Starry night swirls and moonlight",
                "Russian" to "Завихрения звездной ночи и лунный свет",
                "Spanish" to "Remolinos de la noche estrellada y luz de luna",
                "Chinese" to "星空漩涡与月光",
                "Hindi" to "तारों वाली रात के बवंडर और चांदनी",
                "Arabic" to "دوامات ليلة مرصعة بالنجوم وضوء القمر",
                "French" to "Tourbillons de la nuit étoilée et clair de l'une",
                "Portuguese" to "Redemoinhos de noite estrelada e luar",
                "German" to "Wirbel der Sternennacht und Mondlicht",
                "Japanese" to "星月夜のうずまきと月明かり"
            ),
            "AvantGarde" to mapOf(
                "English" to "Constructivism, sharp lines and red circle",
                "Russian" to "Конструктивизм, резкие линии и красный круг",
                "Spanish" to "Constructivismo, líneas afiladas y círculo rojo",
                "Chinese" to "构成主义、犀利线条与红圆圈",
                "Hindi" to "रचनावाद, तेज रेखाएं और लाल घेरा",
                "Arabic" to "البنائية والخطوط الحادة والدائرة الحمراء",
                "French" to "Constructivism, lignes nettes et cercle rouge",
                "Portuguese" to "Construtivismo, linhas nítidas e círculo vermelho",
                "German" to "Konstruktivismus, scharfe Linien und roter Kreis",
                "Japanese" to "構成主義、シャープなラインと赤い円"
            )
        )
        val item = styles[style] ?: return style
        return item[lang] ?: item["English"] ?: style
    }

    fun getAuthorName(handle: String, lang: String): String {
        val authors = mapOf(
            "@NASA" to mapOf(
                "English" to "NASA", "Russian" to "Космическое Агентство NASA", "Spanish" to "NASA en Español",
                "Chinese" to "美国国家航空航天局 NASA", "Hindi" to "नासा अंतरिक्ष केंद्र", "Arabic" to "وكالة ناسا الفضائية",
                "French" to "NASA Spatial", "Portuguese" to "NASA Espacial", "German" to "NASA Weltraum", "Japanese" to "NASA宇宙局"
            ),
            "@TechCrunch" to mapOf(
                "English" to "TechCrunch Daily", "Russian" to "ТехноТренды Daily", "Spanish" to "Noticias Tecnológicas TechCrunch",
                "Chinese" to "科技创投 TechCrunch", "Hindi" to "टेकक्रंच दैनिक", "Arabic" to "تيك كرنش التقني",
                "French" to "Actualités TechCrunch", "Portuguese" to "Notícias TechCrunch", "German" to "TechCrunch Nachrichten", "Japanese" to "テッククランチ技術報"
            ),
            "@Reuters" to mapOf(
                "English" to "Reuters World", "Russian" to "Мировой Вестник Reuters", "Spanish" to "Reuters Noticias Globales",
                "Chinese" to "路透社全球新闻", "Hindi" to "रॉयटर्स विश्व समाचार", "Arabic" to "رويترز العالمية",
                "French" to "Reuters International", "Portuguese" to "Reuters Notícias", "German" to "Reuters Weltnachrichten", "Japanese" to "ロイター世界通信"
            ),
            "@folklore" to mapOf(
                "English" to "Folklore & Wisdom", "Russian" to "Этно-Вестник Мудрости", "Spanish" to "Folclore y Sabiduría",
                "Chinese" to "世界民间传说与智慧", "Hindi" to "लोककथा और ज्ञान", "Arabic" to "الفولكلور والحكمة العالمية",
                "French" to "Folklore et Sagesse", "Portuguese" to "Folclore e Sabedoria", "German" to "Folklore & Weisheit", "Japanese" to "民間伝承と知恵の探求"
            ),
            "@OdessaHumor" to mapOf(
                "English" to "Odessa Humorous Club", "Russian" to "Одесский Клуб Юмора", "Spanish" to "Club del Humor de Odesa",
                "Chinese" to "奥德萨幽默沙龙", "Hindi" to "ओडेसा हास्य क्लब", "Arabic" to "نادي أوديسا الكوميدي",
                "French" to "Club d'Humour d'Odessa", "Portuguese" to "Clube de Humor de Odessa", "German" to "Odessa Humor-Club", "Japanese" to "オデッサ風ユーモア倶楽部"
            ),
            "@space_horizons" to mapOf(
                "English" to "Space Horizons", "Russian" to "Космические Горизонты", "Spanish" to "Horizontes Espaciales",
                "Chinese" to "太空地平线", "Hindi" to "अंतरिक्ष क्षितिज", "Arabic" to "آفاق الفضاء",
                "French" to "Horizons Spatiaux", "Portuguese" to "Horizontes Espaciais", "German" to "Weltraumhorizonte", "Japanese" to "宇宙の地平線"
            ),
            "@tech_trends" to mapOf(
                "English" to "Tech Trends Daily", "Russian" to "Техно Трендс", "Spanish" to "Tendencias Tecnológicas",
                "Chinese" to "每日科技趋势", "Hindi" to "तकनीकी रुझान दैनिक", "Arabic" to "اتجاهات التقنية اليومية",
                "French" to "Tendances Tech", "Portuguese" to "Tendências Tecnológicas", "German" to "Tech-Trends Täglich", "Japanese" to "最新テックトレンド"
            ),
            "@ethno_observer" to mapOf(
                "English" to "Ethno Observer", "Russian" to "Этно Вестник", "Spanish" to "Observador Étnico",
                "Chinese" to "民俗观察家", "Hindi" to "नृवंश प्रेक्षक", "Arabic" to "المراقب الإثني",
                "French" to "L'Observateur Ethno", "Portuguese" to "Observador Étnico", "German" to "Ethno-Beobachter", "Japanese" to "エスノ・オブザーバー"
            ),
            "@global_pulse" to mapOf(
                "English" to "Global Pulse", "Russian" to "Пульс Планеты", "Spanish" to "Pulso Global",
                "Chinese" to "全球脉搏", "Hindi" to "वैश्विक पल्स", "Arabic" to "النبض العالمي",
                "French" to "Pouls Global", "Portuguese" to "Pulso Global", "German" to "Globaler Puls", "Japanese" to "グローバル・パルス"
            )
        )
        val item = authors[handle] ?: return handle.removePrefix("@")
        return item[lang] ?: item["English"] ?: handle.removePrefix("@")
    }

    fun getLoadingPhrases(lang: String): List<String> {
        val list = mapOf(
            "English" to listOf(
                "Brewing hot green tea...",
                "Tuning the lute and drums...",
                "Consulting Armenian Radio...",
                "Chasing a sled in the tundra...",
                "Negotiating commission with Sarah...",
                "Grilling delicious skewers...",
                "Taming Kamchatka brown bears...",
                "Saddling our cowboy horses..."
            ),
            "Russian" to listOf(
                "Завариваем душистый зелёный чай...",
                "Настраиваем саз и дойру...",
                "Спрашиваем Армянское радио...",
                "Ловим попутную упряжку в тундре...",
                "Обсуждаем процент с тётей Сарой...",
                "Жарим сочный шашлык...",
                "Кормим камчатских медведей...",
                "Седлаем коня под техасским небом..."
            ),
            "Spanish" to listOf(
                "Preparando té verde caliente...",
                "Afinando el laúd y los tambores...",
                "Consultando a la Radio Armenia...",
                "Persiguiendo un trineo en la tundra...",
                "Negociando la comisión con Sarah...",
                "Asando deliciosas brochetas...",
                "Domando osos pardos de Kamchatka...",
                "Ensillando nuestros caballos de vaquero..."
            ),
            "Chinese" to listOf(
                "正在冲泡热绿茶...",
                "正在调试琴弦与鼓点...",
                "正在咨询亚美尼亚电台...",
                "正在雪原上追赶雪橇...",
                "正在与萨拉姨妈商量提成...",
                "正在烤香喷喷的烤肉串...",
                "正在驯服堪察加棕熊...",
                "正在德克萨斯天空下备鞍..."
            ),
            "Hindi" to listOf(
                "गर्म हरी चाय बनाना...",
                "सरोद और ढोल ट्यून करना...",
                "आर्मेनियाई रेडियो से परामर्श करना...",
                "टुंड्रा में स्लेज का पीछा करना...",
                "सराह के साथ कमीशन की बातचीत...",
                "स्वादिष्ट कबाब ग्रिल करना...",
                "कमचटका भालू को पालतू बनाना...",
                "चरवाहे घोड़ों को काठी डालना..."
            ),
            "Arabic" to listOf(
                "تحضير الشاي الأخضر الساخن...",
                "ضبط دوزان العود والطبول...",
                "استشارة راديو أرمينيا...",
                "مطاردة زلاجة في التندرا...",
                "التفاوض على العمولة مع سارة...",
                "شواء الكباب اللذيذ...",
                "ترويض الدببة الرمادية في كامتشاتكا...",
                "سرج خيول رعاة البقر..."
            ),
            "French" to listOf(
                "Préparation du thé vert chaud...",
                "Accordage du luth et des tambours...",
                "Consultation de la Radio Arménienne...",
                "Poursuite d'un traîneau dans la toundra...",
                "Négociation de la commission avec Sarah...",
                "Grillade de délicieuses brochettes...",
                "Apprivoisement des ours bruns du Kamchatka...",
                "Serrage de la selle de nos chevaux de cow-boy..."
            ),
            "Portuguese" to listOf(
                "Preparando chá verde quente...",
                "Afinando o alaúde e os tambores...",
                "Consultando a Rádio Armênia...",
                "Perseguindo um trenó na tundra...",
                "Negociando a comissão com Sarah...",
                "Grelhando espetinhos deliciosos...",
                "Domando ursos pardos de Kamchatka...",
                "Selando nossos cavalos de caubói..."
            ),
            "German" to listOf(
                "Heißen grünen Tee aufbrühen...",
                "Laute und Trommeln stimmen...",
                "Armenischen Rundfunk konsultieren...",
                "Einen Schlitten in der Tundra jagen...",
                "Provision mit Sarah verhandeln...",
                "Köstliche Spieße grillen...",
                "Kamtschatka-Braunbären zähmen...",
                "Unsere Cowboypferde satteln..."
            ),
            "Japanese" to listOf(
                "温かい緑茶を淹れています...",
                "リュートと太鼓を調律しています...",
                "アルメニアラジオに問い合わせ中...",
                "ツンドラでそりを追いかけています...",
                "サラおばさんと手数料を交渉中...",
                "美味しい串焼きをグリルしています...",
                "カムチャツカのヒグマを飼い慣らしています...",
                "カウボーイの馬に鞍を置いています..."
            )
        )
        return list[lang] ?: list["English"] ?: emptyList()
    }
}

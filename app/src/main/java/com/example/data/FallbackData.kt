package com.example.data

import com.example.data.database.NewsDigest
import com.example.data.database.RadioStation
import com.example.data.database.SavedJoke

object FallbackData {

    val defaultStations = listOf(
        RadioStation(name = "BBC World Service (News)", streamUrl = "https://stream.live.vc.bbcmedia.co.uk/bbc_world_service", isFavorite = false, isDefault = true),
        RadioStation(name = "Radio Swiss Pop (Pop Hits)", streamUrl = "https://stream.srg-ssr.ch/m/rsp/mp3_128", isFavorite = true, isDefault = true),
        RadioStation(name = "SomaFM Groove Salad (Ambient)", streamUrl = "https://ice1.somafm.com/groovesalad-128-mp3", isFavorite = false, isDefault = true),
        RadioStation(name = "Classic FM (Classical)", streamUrl = "https://stream-mz.planetradio.co.uk/classicfm.mp3", isFavorite = false, isDefault = true),
        RadioStation(name = "Jazz FM (Smooth Jazz)", streamUrl = "https://stream-mz.planetradio.co.uk/jazzfmpa.mp3", isFavorite = false, isDefault = true),
        RadioStation(name = "KEXP Seattle (Indie)", streamUrl = "https://kexp-mp3-128.streamguys1.com/kexp128.mp3", isFavorite = false, isDefault = true),
        RadioStation(name = "Radio Cosmo (World Beat)", streamUrl = "https://wdr-cosmo-live.icecast.wdr.de/wdr/cosmo/live/mp3/128/stream.mp3", isFavorite = false, isDefault = true),
        RadioStation(name = "DFM Club Hits (Dance)", streamUrl = "https://dfm.hostingradio.ru/dfm96.mp3", isFavorite = false, isDefault = true)
    )

    fun getFallbackJokes(culture: String, isRussian: Boolean): List<String> {
        return if (isRussian) {
            getRussianJokes(culture)
        } else {
            getEnglishJokes(culture)
        }
    }

    private fun getRussianJokes(culture: String): List<String> {
        return when (culture.lowercase()) {
            "uzbek", "узбекские" -> listOf(
                "Приходит Ходжа Насреддин на базар и видит: продавец предлагает малюсенькую птичку за сто золотых! Насреддин удивляется:\n— Дорогой, почему воробей стоит целое состояние?\n— Это не воробей, это попугай! Он умеет говорить человеческим голосом!\nНа следующий день Насреддин приносит на базар жирного индюка и требует за него двести золотых. Баи смеются:\n— Эй, Насреддин, индюк стоит максимум пять золотых! Почему двести?\n— Так попугай только говорит, а мой индюк — думает! Кому нужны разговоры, когда есть такая мудрость, вах!",
                "В чайхане почтенные люди спрашивают Насреддина:\n— Ходжа, почему твоя жена всегда молчит, когда ты возвращаешься домой?\nНасреддин заваривает зеленый чай, хмурит брови и шепчет:\n— Понимаете, друзья... Я всегда прихожу домой с полными карманами горячих лепешек и сушеной дыни. А с полным ртом даже самая мудрая женщина не сможет сказать глупости! Золотое правило восточного гостеприимства!",
                "Один хитрый бай решил проверить Насреддина и говорит:\n— Эй, Ходжа, если твой ишак такой умный, научи его читать Коран за год, я дам тебе мешок золота! А если не научишь — отрублю голову.\nНасреддин согласился. Дома жена в слезах:\n— Что ты наделал! Ишак не может читать!\n— Успокойся, дорогая, — улыбнулся Ходжа. — За год либо бай помрет, либо ишак сдохнет, либо я отправлюсь к предкам. Зато целый год мы будем есть плов со свежей бараниной!"
            )
            "armenian", "армянские" -> listOf(
                "Вопрос Армянскому радио:\n— Может ли запорожец развить скорость 200 километров в час?\nАрмянское радио отвечает:\n— Конечно, ара! Если его спустить с вершины горы Арарат прямо в Дилижан на хорошем канатном тросе!",
                "Армянское радио спрашивают:\n— Что такое дружба по-армянски?\nОтвет:\n— Это когда ты приходишь к другу в гости на пять минут попить чаю, а уходишь через три дня, с пятью килограммами шашлыка, рецептом хаша от его бабушки и обещанием крестить его младшего племянника!",
                "Приезжает турист в Ереван, заходит в кафе и просит чашечку кофе. Официант приносит ароматный кофе с горой пахлавы, фруктами и домашним сыром. Турист удивленно говорит:\n— Но я заказывал только кофе!\nОфициант хлопает его по плечу:\n— Слушай, дорогой, у нас в Армении пустой кофе пьют только прокуроры на допросе! Кушай на здоровье, ара, это от души!"
            )
            "jewish", "еврейские" -> listOf(
                "— Сарочка, радость моя, ты не поверишь! Мойша на улице нашел золотые часы!\n— Ой, Абрамчик, а почему они так громко тикают?\n— Та это не часы тикают, это у Мойши от счастья сердце разрывается! А хозяин часов так бежал, так бежал, шо даже забыл их забрать!",
                "Разговаривают два одессита на Привозе:\n— Сёма, ты слышал, Рабинович вчера выиграл в лотерею миллион рублей?\n— Да ты шо! И как он себя чувствует?\n— Плохо, очень плохо... Он теперь спать не может: всё думает, зачем он потратил целый рубль на этот лотерейный билет, если мог просто найти этот миллион на улице бесплатно!",
                "Мойша приходит к раввину:\n— Ребе, у меня дома жена, тёща, пятеро детей, теснота, шум... Я сойду с ума! Что делать?\n— Мойша, купи козу.\nЧерез неделю Мойша прибегает бледный:\n— Ребе, коза гадит, бодается, пахнет, шуму еще больше! Спасите!\n— А теперь, Мойша, продай козу.\nНа следующий день Мойша возвращается сияющий:\n— Ребе, боже мой! Какое счастье, какая тишина и простор без этой козы! Вы гений!"
            )
            "chukchi", "чукотские" -> listOf(
                "Приезжает геолог в тундру и видит: сидит чукча на берегу Оби, рыбу ловит. Геолог спрашивает:\n— Слышь, чукча, скажи, какая завтра погода будет?\nЧукча посмотрел на небо, на воду, понюхал трубку и говорит:\n— Завтра пурга будет, однако! Великий холод пойдет.\nГеолог не поверил, ушел. Назавтра — действительно, страшная пурга. Геолог прибегает в чуме, восхищается:\n— Ну ты даешь, чукча! Как ты узнал? По поведению птиц? По течению реки?\nЧукча ухмыльнулся, затянулся трубкой:\n— Зачем птицы, однако? По радио сказали, великий диктор из Москвы вещал!",
                "Чукча купил холодильник и привез его в ярангу. Другой чукча приходит в гости, удивляется:\n— Однако, зачем тебе холодильник в тундре? На улице минус сорок, в яранге минус десять!\n— Э-э, глупый ты! На улице минус сорок, в холодильнике плюс три. Чукча греться туда ходить будет!",
                "Сидят два чукчи на берегу Ледовитого океана. Вдруг всплывает американская подводная лодка. Капитан открывает люк и кричит:\n— Хэллоу! Куда поплыл русский военный корабль?\nЧукча показывает пальцем налево:\n— Туда поплыл, однако! На северо-запад.\nЛодка погружается. Через час всплывает советская подводная лодка. Капитан кричит:\n— Эй, товарищи! Вы американскую лодку видели? Куда они поплыли?\nЧукча говорит:\n— На северо-запад поплыли, однако!\nКапитан злится:\n— Ты мне не мудри со своими ветрами! Пальцем покажи!"
            )
            "yakut", "якутские" -> listOf(
                "Якутский охотник возвращается в улус после долгой охоты в тайге. Сосед спрашивает:\n— Ну что, Петр, как охота? Медведя встретил?\n— Встретил, однако! Огромный медведь, как гора.\n— И что ты сделал? Стрелял?\n— Зачем стрелял? Ружье дома забыл. Я ему просто улыбнулся самой вежливой якутской улыбкой.\n— И что медведь?\n— От такой вежливости даже у зверя аппетит пропал, ушел спать в берлогу, уважает традиции!",
                "В Якутии наступила зима, термометр показывает минус шестьдесят пять градусов. Журналист из Москвы звонит местному жителю:\n— Скажите, у вас правда так холодно, что птицы на лету замерзают?\n— Да нет, тепло у нас, около тридцати пяти.\n— Но по телевизору передали, что у вас минус шестьдесят пять!\n— А-а-а, так это на улице! А дома-то тепло, мы печку топим, чай горячий с брусникой пьем!",
                "Два якутских шамана поспорили, чей бубен громче вызывает дождь. Первый бил три часа — пошел мелкий грибной дождик. Второй бил пять минут — хлынул ливень, затопил все юрты. Первый шаман уважительно качает головой:\n— Ну ты силен! Какое заклинание читал?\nВторой вытирает пот со лба:\n— Да заклинание простое: перед тем как в бубен бить, я прогноз погоды в интернете на смартфоне посмотрел. Против спутникового гидрометцентра никакой шаман не сдюжит!"
            )
            "kamchatka", "камчатские" -> listOf(
                "Приезжают туристы на Камчатку, гид ведет их по Долине Гейзеров и предупреждает:\n— Осторожно, здесь кругом медведи! Если увидите медведя — ни в коем случае не бегите. Бегущий турист для медведя — это фастфуд на колесиках.\n— А что делать?\n— Начните громко петь песни. Медведи очень не любят фальшивое пение. Особенно если вы будете петь караоке без музыки, они сразу уходят в сопки от греха подальше!",
                "Разговаривают два камчатских краба на дне Охотского моря:\n— Слышал, людей на берегу совсем лихорадит? Икру ложками едят, крабами закусывают...\n— Да уж, странные существа. Вместо того чтобы тихо жить на дне в прохладе, они лезут на вершины действующих вулканов, чтобы сделать селфи на фоне кипящей лавы! И кто после этого примитивный вид?",
                "На Камчатке идет сильнейший снегопад, засыпало дома по самые крыши. Житель Петропавловска пытается откопать свою машину. Копает час, два... Вдруг лопата бьется обо что-то металлическое. Радостно откапывает стекло, а там сидит водитель троллейбуса и говорит:\n— Мужик, отойди, не мешай! Я уже третьи сутки расписание догоняю, а тут ты со своей малолитражкой!"
            )
            "american", "американские" -> listOf(
                "Приходит ковбой в техасский бар, выпивает виски и видит: у входа стоит огромный черный конь. Он заходит обратно, бьет кулаком по столу и кричит:\n— Кто покрасил мою лошадь в зеленый цвет?! Это оскорбление!\nВстает огромный двухметровый детина со шрамом на лице, поигрывая револьвером, и басит:\n— Ну, я покрасил. А в чем проблема, сынок?\nКовбой моментально поправляет шляпу, улыбается во весь рот и говорит:\n— О, сэр, никаких проблем! Я просто хотел сказать, что первый слой уже высох, можете наносить второй!",
                "Уолл-стрит. Молодой брокер хвастается перед опытным коллегой:\n— Я сегодня заработал пятьдесят тысяч долларов за пять минут!\nСтарший брокер вздыхает, отпивая дорогой коньяк:\n— Молодец, сынок. А теперь скажи мне: сколько тысяч долларов тебе понадобится заплатить адвокатам, чтобы доказать судье, что это было законно?",
                "В Техасе один фермер хвастается другому:\n— Мое ранчо такое огромное, что если я сяду утром в свой грузовик, то только к вечеру смогу доехать до противоположной границы моего забора!\nВторой фермер сочувственно кивает:\n— Да уж, парень... У меня тоже когда-то был такой старый и паршивый грузовик, но слава богу, я его продал!"
            )
            "african", "африканские" -> listOf(
                "В африканской саванне лев созывает всех зверей и объявляет:\n— Так, звери! Сегодня делимся на две группы. Умные — налево, красивые — направо!\nВсе звери разошлись. Обезьяна мечется посередине. Лев рычит:\n— Эй, мартышка, ты чего зависла?\nОбезьяна вздыхает:\n— Ну не могу же я разорваться! Я ведь и умная, и безумно красивая одновременно! Нам, приматам, всегда тяжелее всех жить!",
                "Турист на сафари в Африке спрашивает местного проводника:\n— Скажите, а эти львы действительно нападают на людей только тогда, когда голодны?\nПроводник улыбается белозубой улыбкой:\n— Да, белый господин! Но дело в том, что в нашей жаркой саванне у льва аппетит есть абсолютно всегда! Он считает туриста очень деликатным и легким йогуртом перед настоящей охотой на антилопу!",
                "Молодой охотник из племени приходит к вождю:\n— Вождь, я сегодня столкнулся в зарослях лицом к лицу с огромной гориллой! Она посмотрела на меня, забрала мое копье, согнула его пополам и бросила мне под ноги!\nВождь задумчиво покурил трубку и ответил:\n— Радуйся, сын мой! Очень хорошо, что она согнула твое копье, а не тебя. Гориллы — очень миролюбивые существа, пока их не просят сыграть в баскетбол твоей головой!"
            )
            else -> listOf(
                "Один мудрец сказал: 'Юмор — это международный язык.' И действительно, хорошая шутка понятна в любой точке земного шара, от тундры до знойной саванны!",
                "Заходят в чайхану узбек, армянин и одессит. Чайханщик смотрит на них и говорит: 'О, кажется, сейчас родится отличный анекдот!'",
                "Сидят двое на завалинке, слушают радио. Один говорит: 'Какая душевная музыка!'. Второй отвечает: 'Да, это потому, что под неё анекдоты читаются в два раза смешнее!'"
            )
        }
    }

    private fun getEnglishJokes(culture: String): List<String> {
        return when (culture.lowercase()) {
            "uzbek", "узбекские" -> listOf(
                "Hodja Nasreddin comes to the market and sees a man selling a tiny bird for a hundred gold coins! Hodja asks:\n— My dear friend, why does a sparrow cost a whole fortune?\n— This is no sparrow, it is a talking parrot! He can speak like a human!\nThe next day, Nasreddin brings a fat turkey to the market and demands two hundred gold coins. The rich merchants laugh:\n— Nasreddin, a turkey costs five coins at most! Why two hundred?\n— Well, that parrot only talks, but my turkey actually thinks! Who needs empty words when you can have such deep wisdom!",
                "In a teahouse, respectables ask Nasreddin:\n— Hodja, why does your wife always stay silent when you return home?\nHodja pours some green tea, smiles and whispers:\n— You see, my friends... I always come home with my pockets full of hot flatbreads and sweet dried melons. And with a mouth full of delicious treats, even the wisest woman can't say anything foolish!",
                "A cunning rich landlord decided to test Nasreddin and says:\n— Hodja, if your donkey is so smart, teach him to read the holy book in one year, and I'll give you a bag of gold! If you fail, I will chop your head off.\nNasreddin agreed. At home, his wife was in tears:\n— What have you done! A donkey cannot read!\n— Calm down, my dear, — Hodja smiled. — In one year, either the landlord will die, the donkey will pass away, or I will go to the ancestors. Meanwhile, we will eat delicious plov with fresh lamb for a whole year!"
            )
            "armenian", "armenian" -> listOf(
                "Armenian Radio was asked:\n— Is it possible for a cheap domestic car to reach a speed of 200 kilometers per hour?\nArmenian Radio answers:\n— Of course, my friend! If you push it off the very peak of Mount Ararat straight down to Dilijan tied to a very strong rope!",
                "Armenian Radio was asked:\n— What is friendship in Armenian style?\nAnswer:\n— It is when you visit a friend's house for five minutes just to grab a quick tea, and you end up leaving three days later with five kilograms of barbecue, his grandmother's special recipe, and a promise to be the godfather of his nephew's cousin!",
                "A tourist arrives in Yerevan, enters a local cafe, and asks for a simple cup of coffee. The waiter brings an aromatic coffee alongside a mountain of baklava, fresh fruits, and homemade cheese. The tourist says:\n— But I only ordered a coffee!\nThe waiter pats him on the shoulder:\n— Look, my dear, in Armenia, only prosecutors drink plain coffee during interrogations! Eat in good health, it comes from the heart!"
            )
            "jewish", "еврейские" -> listOf(
                "— Sarah, my love, you won't believe it! Moishe found a gold watch on the street!\n— Oh, Abram, why is it ticking so loudly?\n— It's not the watch ticking, it's Moishe's heart pounding from joy! And the owner of the watch ran so fast, he even forgot to take it back!",
                "Two Odessa citizens talk in the market:\n— Syoma, did you hear? Rabinovich won a million in the lottery yesterday!\n— Wow! And how does he feel?\n— Terrible, simply terrible... He can't sleep now. All he thinks about is why he wasted a whole ruble on that lottery ticket when he could have just found that million on the street for free!",
                "Moishe comes to the rabbi:\n— Rabbi, my house is a nightmare! My wife, my mother-in-law, five kids, it's crowded and loud... I'm going crazy! What should I do?\n— Moishe, buy a goat.\nA week later, Moishe runs back, pale and exhausted:\n— Rabbi, the goat is making a mess, biting everyone, smells awful, it's ten times worse!\n— Now, Moishe, sell the goat.\nThe next day, Moishe returns glowing with happiness:\n— Rabbi, oh my god! Such peace, such quiet and space without that goat! You are an absolute genius!"
            )
            "chukchi", "чукотские" -> listOf(
                "A geologist comes to the tundra and sees a Chukchi man fishing. The geologist asks:\n— Hey, what will the weather be like tomorrow?\nThe Chukchi looks at the sky, sniffs his pipe, and says:\n— Tomorrow, a heavy blizzard will come, indeed! Very cold weather.\nThe geologist didn't believe him and left. The next day, a massive blizzard hit. The geologist returned to the tent, amazed:\n— How did you know? By the birds? By the river current?\nThe hunter smiled:\n— Why birds? It was on the radio! The big announcer in Moscow spoke!",
                "A Chukchi bought a refrigerator and brought it to his tent. His friend comes over, surprised:\n— Why do you need a fridge in the arctic? It's minus forty outside, minus ten in the tent!\n— Oh, you are silly! It is minus forty outside, but inside the fridge it is plus three. I will go inside to warm up!",
                "Two Chukchi men are sitting on the shore of the Arctic Ocean. Suddenly, an American submarine surfaces. The captain opens the hatch and shouts:\n— Hello! Which way did the Russian warship go?\nThe Chukchi points to the left:\n— That way! North-west, indeed.\nThe submarine submerges. An hour later, a Soviet submarine surfaces. The captain shouts:\n— Comrades! Have you seen the American sub? Which way did they go?\nThe Chukchi says:\n— To the north-west, indeed!\nThe captain gets angry:\n— Don't confuse me with your winds! Just point your finger!"
            )
            "yakut", "якутские" -> listOf(
                "A Yakut hunter returns to his village after a long hunt. A neighbor asks:\n— Well, Pyotr, did you meet any bear?\n— Yes, indeed! A massive bear, as big as a mountain.\n— And what did you do? Did you shoot?\n— Why shoot? I left my rifle at home. I just gave him the warmest, most polite Yakut smile.\n— And the bear?\n— Well, with such politeness, even the beast lost his appetite, walked away to his cave, respecting the ancient local traditions!",
                "Winter came to Yakutia, the thermometer shows minus sixty-five degrees. A reporter from Moscow calls a resident:\n— Tell me, is it true that it's so cold that birds freeze mid-flight?\n— Oh no, it's quite warm here, around minus thirty-five.\n— But the TV news said it's minus sixty-five!\n— Ah, well, that's outside! Inside the house it's warm, we keep the fireplace going and drink hot berry tea!",
                "Two shamans argued over whose drum could summon rain faster. The first beat his drum for three hours — a light drizzle started. The second beat his for five minutes — a massive storm flooded the area. The first shaman nodded respectfully:\n— Wow! What spell did you use?\nThe second wiped sweat from his forehead:\n— No spell! Before beating the drum, I checked the weather forecast on my smartphone. No shaman can beat a satellite meteorological center!"
            )
            "kamchatka", "камчатские" -> listOf(
                "Tourists arrive in Kamchatka, and the guide warns them:\n— Be careful, there are bears everywhere! If you see a bear, never run. A running tourist is just fast food on wheels for them.\n— Then what should we do?\n— Start singing songs loudly. Bears absolutely hate bad singing. Especially if you sing karaoke without music, they immediately run away into the hills to save their ears!",
                "Two Kamchatka crabs talk at the bottom of the Sea of Okhotsk:\n— Did you hear that humans on land are crazy? They eat caviar by the spoonful and feast on crabs...\n— Yes, weird creatures. Instead of living peacefully in the cool sea, they climb to the tops of active volcanoes just to take a photo with boiling lava! And they call us primitive?",
                "Kamchatka is hit by a massive blizzard, burying houses up to the roofs. A resident of Petropavlovsk tries to dig out his car. He digs for two hours. Suddenly, his shovel hits some metal. He happily clears the snow and finds a bus window, with the driver inside saying:\n— Hey pal, back off! I've been trying to catch up with my schedule for three days, and you're blocking me with your small shovel!"
            )
            "american", "американские" -> listOf(
                "A cowboy enters a Texas saloon, downs a shot of whiskey, and sees his black horse painted green outside. He walks back in, slams his fist on the table and yells:\n— Who painted my horse green?! This is an insult!\nA giant, scary two-meter tall outlaw stands up, spinning his revolver, and growls:\n— I did. Any problem, kid?\nThe cowboy quickly adjusts his hat, smiles warmly and says:\n— Oh, no problem at all, sir! I just wanted to tell you that the first coat is already dry, you can go ahead and apply the second one!",
                "On Wall Street, a young broker boasts to an experienced colleague:\n— I made fifty thousand dollars in just five minutes today!\nThe senior broker sighs, sipping his expensive cognac:\n— Good job, kid. Now tell me: how many thousands will you have to pay the lawyers to prove to the judge that it was legal?",
                "In Texas, one rancher boasts to another:\n— My ranch is so huge, that if I get into my truck in the morning, it takes me until evening just to drive to the opposite fence line!\nThe second rancher nods sympathetically:\n— Yeah, I know... I used to have a terrible, slow truck like that too, but thank god, I sold it!"
            )
            "african", "африканские" -> listOf(
                "In the African savannah, a lion gathers all the animals and announces:\n— Alright, animals! Today we divide into two groups. Smart ones to the left, beautiful ones to the right!\nAll animals split up. An monkey is running back and forth in the middle. The lion roars:\n— Hey monkey, why are you stuck?\nThe monkey sighs:\n— Well, I can't split in half! I'm both extremely smart and incredibly beautiful at the same time! Life is always hardest for us primates!",
                "A tourist on safari in Africa asks the local guide:\n— Tell me, do these lions really attack humans only when they are hungry?\nThe guide smiles:\n— Yes, sir! But the thing is, in our hot savannah, lions are hungry absolutely all the time! They view a tourist as a very light, delicious yogurt before chasing a real zebra!",
                "A young hunter from the tribe comes to the chief:\n— Chief, today I met a giant gorilla face-to-face in the jungle! It looked at me, took my spear, bent it in half, and threw it at my feet!\nThe chief puffed his pipe and answered:\n— Be happy, my son! It's very good that it bent your spear and not your spine. Gorillas are very peaceful creatures, as long as you don't ask them to play basketball using your head!"
            )
            else -> listOf(
                "A wise man once said: 'Humor is an international language.' A good joke is understood everywhere, from the tundra to the savannah!",
                "An Uzbek, an Armenian, and an Odessa citizen enter a tea house. The owner looks at them and says: 'Ah, a great joke is about to happen!'",
                "Two people sit listening to the radio. One says: 'Such soulful music!' The second replies: 'Yes, it makes the jokes we read twice as funny!'"
            )
        }
    }

    val defaultNewsDigests = listOf(
        NewsDigest(
            title = "X.com Buzz: The Rise of AI Programmers",
            content = "Tech accounts are actively discussing whether AI will completely replace software developers within the next two years. Opinions are split between optimism about rapid progress and warnings of excessive hype.",
            attachedJoke = "— Сарочка, ты слышала? Искусственный интеллект теперь умеет писать программы за людей!\n— Ой, Моня, я тебя умоляю! А сможет ли этот ваш интеллект объяснить моей маме, почему у неё зависла стиральная машина, так, шоб она не обозвала его бестолочью?",
            attachedJokeCulture = "Jewish"
        ),
        NewsDigest(
            title = "Global News: The Great Watermelon Harvest of Uzbekistan",
            content = "Farmers in Central Asia report a historic surplus in watermelon harvests this year. Markets are flooded with juicy fruits, sparking discussions about export corridors and massive regional watermelon festivals.",
            attachedJoke = "Приходит Ходжа Насреддин на базар и видит бай продает арбузы по сто золотых. Насреддин берет арбуз, надкусывает и говорит:\n— Дорогой, твой арбуз сладкий как мед, но почему цена золотая?\n— Так это потому, что он укрепляет память, глупец!\nНасреддин отдает сто золотых, съедает арбуз и вдруг вскрикивает:\n— Постой, на соседнем ряду такие же арбузы лежат по два медных гроша! Я же переплатил!\nПродавец торжествующе улыбается:\n— Ну вот видишь, дорогой! Не успел доесть, а память уже укрепилась! С тебя еще два золотых за скорость эффекта!",
            attachedJokeCulture = "Uzbek"
        ),
        NewsDigest(
            title = "Space X Trend: Mars Colonization Timelines Challenged",
            content = "Space enthusiasts are debating Elon Musk's ambitious timelines for sending the first human colony to Mars. Scientists highlight challenges of radiation, soil toxicity, and psychological stress during the 9-month trip.",
            attachedJoke = "Чукча сидит на берегу тундры и смотрит на ночное небо. К нему подходит геолог и говорит:\n— Слышь, чукча, скоро американцы и русские на Марс полетят! Будут там яранги строить, оленей разводить!\nЧукча подумал, попыхтел трубкой и отвечает:\n— Оленей разводить на Марсе — это хорошо, однако! Там белых медведей нету, воровать оленей некому. Но скажи, геолог, как туда чукотский ишак с грузом дойдет? Девять месяцев пешком идти, однако, никакого сена не хватит!",
            attachedJokeCulture = "Chukchi"
        ),
        NewsDigest(
            title = "Entertainment: Global Retro Radio Boom",
            content = "Music critics observe a massive global surge in internet radio streaming. Audiences are shifting away from randomized algorithms back to curated, human-hosted radio broadcasts for nostalgia and community feeling.",
            attachedJoke = "Вопрос Армянскому радио:\n— Правда ли, что под хорошую радиостанцию арбузы растут в два раза слабее, а шашлык жарится в два раза быстрее?\nАрмянское радио отвечает:\n— Конечно, правда, ара! Под хорошую музыку даже шампур сам крутится, а гости съедают мясо еще до того, как хозяин успеет объявить первый тост!",
            attachedJokeCulture = "Armenian"
        ),
        NewsDigest(
            title = "Climate Watch: Record Cold Temperatures in Yakutia",
            content = "The Oymyakon region recorded winter temperatures dipping below minus sixty-eight degrees Celsius. Schools are closed, but locals remain calm, sharing videos of pure boiling water turning instantly into snow in the air.",
            attachedJoke = "В Якутии наступил мороз минус семьдесят градусов. Журналист спрашивает у местного охотника:\n— Как вы согреваетесь в такой дикий холод?\n— Ну как, однако... Берем чашку горячего чая, выходим на улицу. Чай замерзает прямо в воздухе. Мы лед грызем — внутри сразу тепло становится! Главное — бубен сильно не бить, а то сосульки с ушей отвалятся, однако!",
            attachedJokeCulture = "Yakut"
        )
    )

    fun getEnglishNewsDigests(): List<NewsDigest> {
        return listOf(
            NewsDigest(
                title = "X.com Buzz: The Rise of AI Programmers",
                content = "Tech accounts are actively discussing whether AI will completely replace software developers within the next two years. Opinions are split between optimism about rapid progress and warnings of excessive hype.",
                attachedJoke = "— Sarah, did you hear? Artificial intelligence can now write programs instead of humans!\n— Oh, Monya, I beg you! But will this intelligence of yours be able to explain to my mother why her washing machine froze, without her calling him a good-for-nothing?",
                attachedJokeCulture = "Jewish"
            ),
            NewsDigest(
                title = "Global News: The Great Watermelon Harvest of Uzbekistan",
                content = "Farmers in Central Asia report a historic surplus in watermelon harvests this year. Markets are flooded with juicy fruits, sparking discussions about export corridors and massive regional watermelon festivals.",
                attachedJoke = "Hodja Nasreddin comes to the market and sees a rich merchant selling watermelons for a hundred gold coins. Nasreddin takes a bite and says:\n— My friend, your watermelon is sweet as honey, but why is it so expensive?\n— Because it improves your memory, you fool!\nNasreddin pays, eats it, and suddenly yells:\n— Wait! On the next row, they sell the exact same watermelons for two copper coins! I overpaid!\nThe merchant triumphs:\n— You see, my friend! You haven't even finished it, and your memory is already improved! That will be two more gold coins for the speed of the effect!",
                attachedJokeCulture = "Uzbek"
            ),
            NewsDigest(
                title = "Space X Trend: Mars Colonization Timelines Challenged",
                content = "Space enthusiasts are debating Elon Musk's ambitious timelines for sending the first human colony to Mars. Scientists highlight challenges of radiation, soil toxicity, and psychological stress during the 9-month trip.",
                attachedJoke = "A Chukchi man sits in the tundra looking at the starry sky. A geologist walks up and says:\n— Hey, soon we will fly to Mars! We will build tents and raise reindeer there!\nThe hunter thinks for a bit, puffs his pipe, and says:\n— Raising reindeer on Mars is good, indeed! No polar bears to steal them. But tell me, geologist, how will our donkeys get there with the cargo? Nine months of walking, indeed, no hay will ever be enough!",
                attachedJokeCulture = "Chukchi"
            ),
            NewsDigest(
                title = "Entertainment: Global Retro Radio Boom",
                content = "Music critics observe a massive global surge in internet radio streaming. Audiences are shifting away from randomized algorithms back to curated, human-hosted radio broadcasts for nostalgia and community feeling.",
                attachedJoke = "Armenian Radio was asked:\n— Is it true that with a good radio stream playing, watermelons grow slower but barbecues grill twice as fast?\nArmenian Radio answers:\n— Of course, my friend! With good music, even the skewers turn themselves, and the guests eat all the meat before the host can even finish his first toast!",
                attachedJokeCulture = "Armenian"
            ),
            NewsDigest(
                title = "Climate Watch: Record Cold Temperatures in Yakutia",
                content = "The Oymyakon region recorded winter temperatures dipping below minus sixty-eight degrees Celsius. Schools are closed, but locals remain calm, sharing videos of pure boiling water turning instantly into snow in the air.",
                attachedJoke = "Yakutia hit minus seventy degrees. A reporter asks a local hunter:\n— How do you stay warm in such freezing cold?\n— Well, indeed... We take a cup of hot tea and go outside. The tea freezes instantly in the air. We just chew the sweet ice and we feel warm inside! Just don't beat your drum too hard, or your frozen ears will fall off, indeed!",
                attachedJokeCulture = "Yakut"
            )
        )
    }
}

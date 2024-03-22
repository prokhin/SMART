Тестовое задание
Необходимо используя Akka HTTP Java реализовать api для регистрации и авторизации пользователя в сервисе. 
Методы, которые должны быть реализованы:


curl --request POST --location 'https://dev.skif.pro/api_v1/registrate' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "mshevelevich@gmail.com",
    "password": "himmih1234",
    "name": "Миша"
}'
Код 200 и пустой body в случае успеха.
Если уже есть аккаунт с таким email возращается Код 422 и body c ошибкой {  "error": "session.errors.emailAlreadyRegistered" }


curl --request POST --location 'https://dev.skif.pro/api_v1/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "test1@ya.ru",
  "password": "12345N"
}'

В случае успешной авторизации возвращается пусто с кодом 200 c пустым body
В случае не успешной авторизации возвращается status: 422 и json-body с ошибкой
curl --location 'https://dev.skif.pro/api_v1/me'
В случае авторизованного пользователя возвращается код 200 и body:
{
  "id": "75bd17cc-642f-4c7c-982e-e0e991907733",    //  id пользователя
  "email": "test1@ya.ru", // email пользователя
  "created": "2017-03-10 16:56:41",          //  дата создания
  "name": "Миша"    //  имя пользователя
}
В случае неавторизованного пользователя код 401 и пустым body


curl --location --request PUT 'https://dev.skif.pro/api_v1/logout'
Всегда возвращает 200 и пустой body
Необходимы тесты:
После регистрации есть возможность залогинится
Нет возможности залогинится под ранее незарегистрированным учетным данным
Можно получить данные пользователя только после успешного логина

(ns T01
  (:require
    [nextjournal.clerk :as clerk]
    [shop_app_reusables :as r]
    )
  )



(require '[datomic.client.api :as d])
(def client (d/client {:server-type :dev-local
                       :storage-dir :mem
                       :system      "ci"}))
(d/create-database client {:db-name "db01"})
(def conn (d/connect client {:db-name "db01"}))
(def db (d/db conn))                                        ;;refresh database

(def db-schema
  [{:db/ident       :product/id
    :db/valueType   :db.type/long
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}
   {:db/ident       :product/label
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       :product/type
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       :product/model-no
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])
(d/transact conn {:tx-data db-schema})
(def db (d/db conn))                                        ;;refresh database


(def stock-schema
  [{:db/ident       :stock/product
    :db/valueType   :db.type/ref
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}
   {:db/ident       :stock/amount
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}])
(d/transact conn {:tx-data stock-schema})
(def db (d/db conn))                                        ;;refresh database


(def order-schema
  [{:db/ident       :order/product
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident       :order/user
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident       :order/size
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}])
(d/transact conn {:tx-data order-schema})
(def db (d/db conn))                                        ;;refresh database


(def user-schema
  [{:db/ident       :user/id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident       :user/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       :user/password
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])
(d/transact conn {:tx-data user-schema})
(def db (d/db conn))                                        ;;refresh database


(def product-data
  [{:product/id       100
    :product/label    "lacoste"
    :product/type     "urban clothing"
    :product/model-no "polo shirt"}
   {:product/id       101
    :product/label    "canada goose"
    :product/type     "jackets"
    :product/model-no "caban"}
   {:product/id       102
    :product/label    "mammut"
    :product/type     "boots"
    :product/model-no "hiking boots"}
   {:product/id       103
    :product/label    "husky"
    :product/type     "sleeping bags"
    :product/model-no "arnapurna"}])
(d/transact conn {:tx-data product-data})
(def db (d/db conn))                                        ;;refresh database

(def stock-data
  [{:stock/product (r/get-entity-id-by-label db "lacoste")
    :stock/amount  10}
   {:stock/product (r/get-entity-id-by-label db "canada goose")
    :stock/amount  8}
   {:stock/product (r/get-entity-id-by-label db "mammut")
    :stock/amount  6}
   {:stock/product (r/get-entity-id-by-label db "husky")
    :stock/amount  4}])
(d/transact conn {:tx-data stock-data})


(def user-data
  [{:user/id       1
    :user/name     "admin01"
    :user/password "123456"}
   {:user/id       2
    :user/name     "userdemo"
    :user/password "123456"}
   ])
(d/transact conn {:tx-data user-data})


;; ## ---------------------------------------------------------------------------------------------------------------
;1. ## Launch browser
;2. ## Navigate to url 'http://localhost:7779/'
;3. ## Click on 'Signup / Login' button
(clerk/html [:button {:type "button"} "Signup / Login"])
;4. ## Enter username password and email then  click 'Signup' button to create an account
(clerk/html [:div.text-field-container [:form#userForm {:class ""}
                                        [:div#userName-wrapper.mt-2.row [:div.col-md-3.col-sm-12 [:label#userName-label.form-label "Name/Id  "]] [:div.col-md-9.col-sm-12 [:input#userName.mr-sm-2.form-control {:autocomplete "off" :placeholder "Username goes here" :type "text"}]]]
                                        [:div#userEmail-wrapper.mt-2.row [:div.col-md-3.col-sm-12 [:label#userEmail-label.form-label "Email"]] [:div.col-md-9.col-sm-12 [:input#userEmail.mr-sm-2.form-control {:autocomplete "off" :placeholder "name@example.com" :type "email"}]]]
                                        [:div#userEmail-wrapper.mt-2.row [:div.col-md-3.col-sm-12 [:label#userEmail-label.form-label "Password"]] [:div.col-md-9.col-sm-12 [:input#userEmail.mr-sm-2.form-control {:autocomplete "off" :placeholder "Password" :type "Password"}]]]
                                        [:div.mt-2.justify-content-end.row [:div.text-right.col-md-2.col-sm-12 [:button#submit.btn.btn-primary {:type "button"} "Submit"]]] [:div#output.mt-4.row [:div.undefined.col-md-12.col-sm-12]]]])


(r/add-new-user db conn 3 "bariscan" "123456")

;5. ## user clicks products page to see all products
;   ## products page!
;6. ## user sees all of the products

(r/show-all-products db)

(r/get-entity-id-by-label db "husky")
(d/q
  '[:find ?name ?p
    :where
    [?e :stock/product ?p]
    [?e :stock/amount ?name]]
  db)
(d/q
  '[:find ?size
    :in $ ?entity-id
    :where
    [?e :stock/product ?entity-id]
    [?e :stock/amount ?size]]
  db (r/get-entity-id-by-label db "mammut"))
(identity r/cart-info)
;7. ## user puts a mammut boot in the cart and completes the purchase function
(r/put-item-in-cart db "bariscan" "mammut" 1)
(r/put-item-in-cart db "bariscan" "husky" 1)
;=> [["bariscan" "mammut" 3]]
(r/stock-check-by-label db "mammut")
(r/sell-all-items-in-cart db conn)
;=> []



(def db (d/db conn))                                        ;;refresh database
(r/stock-check-by-label db "mammut")


;8. ## user return products page
;   ## products page!




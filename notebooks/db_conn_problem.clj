(ns db_conn_problem)

(require '[datomic.client.api :as d])
(def client (d/client {:server-type :dev-local
                       :storage-dir :mem
                       :system      "ci"}))
(d/create-database client {:db-name "db01"})
(def conn (d/connect client {:db-name "db01"}))
(def db (d/db conn))                                        ;;refresh database

(def db-schema
  [{:db/ident       :item/name
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}
   {:db/ident       :item/sum
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}])
(d/transact conn {:tx-data db-schema})

(def item1
  [{:item/name "String"
    :item/sum  0}
   ])
(d/transact conn {:tx-data item1})

(def item2
  [{:item/name    "String"
    :item/sum 10}])
(d/transact conn {:tx-data item2})

(d/q
  '[:find ?sum
    :in $ ?name
    :where
    [?e :item/name ?name]
    [?e :item/sum ?sum]]
  db "String")
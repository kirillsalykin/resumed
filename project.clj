(defproject org.akvo.resumed "0.1.0-SNAPSHOT"
  :description "A Ring handler to support tus.io protocol"
  :url "http://akvo.org"
  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :repositories [["jcenter" {:url "http://jcenter.bintray.com"
                             :snapshots false
                             :checksum :fail
                             :update :always}]]
  :profiles {:dev {:resource-paths ["test/resources"]
                   :dependencies [[ring/ring-core "1.4.0"]
                                  [ring/ring-jetty-adapter "1.4.0"]
                                  [ring/ring-mock "0.3.0"]
                                  [io.tus.java.client/tus-java-client "0.1.4"]]}})

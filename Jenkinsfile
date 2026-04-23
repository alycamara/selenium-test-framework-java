pipeline {

    /*
     * Indique à Jenkins d’exécuter ce pipeline
     * sur n’importe quel agent disponible.
     *
     * Si plusieurs nodes Jenkins existent,
     * Jenkins choisira automatiquement un agent libre.
     */
    agent any


    /*
     * Déclaration des outils installés dans Jenkins.
     *
     * Les noms doivent correspondre exactement
     * aux noms configurés dans :
     * Manage Jenkins > Global Tool Configuration
     */
    tools {

        // Utilisation de Maven pour compiler et lancer les tests
        maven 'maven-3.9.10'

        // Utilisation du JDK Java 17
       // jdk 'JDK17'
    }


    /*
     * Variables d’environnement accessibles
     * dans tout le pipeline.
     *
     * Ici, URL du Selenium Grid utilisé
     * pour exécuter les tests distants.

    environment {

        GRID_URL = 'http://localhost:4444'
    }
*/

    /*
     * Les stages représentent les étapes principales
     * du pipeline CI/CD.
     *
     * Elles seront visibles graphiquement dans Jenkins.
     */
    stages {




        stage('System Info') {
            steps {
                sh 'uname -a'
                sh 'java -version'
                sh 'mvn -version'
            }
        }


        /*
         * Étape 1 : Récupération du code source
         * depuis le repository GitHub.
         */
        stage('Checkout') {

            steps {

                /*
                 * Clone la branche main du projet GitHub.
                 *
                 * Remplacer TON_USERNAME par ton vrai compte GitHub.
                 */
                git branch: 'main',
                    url: 'https://github.com/alycamara/selenium-test-framework-java.git'
            }
        }


        /*
         * Étape 2 : Nettoyage du projet Maven.
         *
         * Supprime anciens fichiers compilés,
         * anciens rapports et anciens artefacts.
         * Build propre pour éviter les conflits avec les anciens résultats sans les test
         *
         */
        stage('Clean Build') {

            steps {

                /*
                 * Commande Linux.
                 * Si Jenkins Windows, utiliser la commande suivante :
                 * bat 'mvn clean'
                 */
                sh 'mvn clean install -DskipTests'
            }
        }


        /*
         * Étape 3 : Lancement des tests automatisés.
         *
         * Maven exécutera :
         * - compilation
         * - TestNG
         * - Selenium
         * - génération des rapports
         */
        stage('Run Tests') {

            steps {

                /*
                 * Lance tous les tests définis
                 * dans le pom.xml / testng.xml
                 */
                sh 'mvn test'
            }
        }


        /*
         * Étape 4 : Publication du rapport HTML.
         *
         * Ici rapport Extent Report généré
         * dans target/ExtentReport
         */
        stage('Publish Test Report') {

            steps {

                publishHTML(target: [

                    /*
                     * Si rapport absent,
                     * le build continue.
                     */
                    allowMissing: true,

                    /*
                     * Toujours afficher le dernier rapport
                     * dans Jenkins.
                     */
                    alwaysLinkToLastBuild: true,

                    /*
                     * Conserver les rapports
                     * de tous les builds précédents.
                     */
                    keepAll: true,

                    /*
                     * Dossier contenant le rapport HTML.
                     */
                    reportDir: 'target/ExtentReport',

                    /*
                     * Fichier principal du rapport.
                     */
                    reportFiles: 'ExtentReport.html',

                    /*
                     * Nom affiché dans Jenkins.
                     */
                    reportName: 'Extent Report'
                ])
            }
        }
    }


    /*
     * Bloc post :
     * Actions exécutées après les stages,
     * quel que soit le résultat.
     */
    post {


        /*
         * Toujours exécuter cette action,
         * succès ou échec.
         */
        always {

            /*
             * Publication des résultats JUnit/TestNG XML.
             *
             * Permet à Jenkins d’afficher :
             * - nombre de tests
             * - succès
             * - échecs
             * - tendances historiques
             */
            junit 'target/surefire-reports/*.xml'
        }


        /*
         * Si pipeline terminé avec succès.
         */
        success {

            echo 'Build SUCCESS'
        }


        /*
         * Si pipeline en erreur.
         */
        failure {

            echo 'Build FAILED'
        }
    }
}
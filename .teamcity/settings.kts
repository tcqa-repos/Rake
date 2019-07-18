import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2019.1"

project {

    vcsRoot(HttpsGithubComSensuPluginsSensuPluginsDockerRefsHeadsMaster)
    vcsRoot(HttpsGithubComRubyRakeRefsHeadsMaster)

    buildType(Build1)
    buildType(Test)
    buildType(Build2)
    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    steps {
        step {
            type = "rake-runner"
        }
    }
})

object Build1 : BuildType({
    name = "Build (1)"

    vcs {
        root(HttpsGithubComSensuPluginsSensuPluginsDockerRefsHeadsMaster)
    }

    steps {
        script {
            id = "RUNNER_115"
            scriptContent = "echo jetbrains | sudo -S bundler install"
        }
        step {
            id = "RUNNER_110"
            type = "rake-runner"
            param("build-file-path", "Rakefile")
        }
        stepsOrder = arrayListOf("RUNNER_115", "RUNNER_110")
    }

    triggers {
        vcs {
        }
    }
})

object Build2 : BuildType({
    name = "Build (2)"

    vcs {
        root(HttpsGithubComRubyRakeRefsHeadsMaster)
    }

    steps {
        script {
            id = "RUNNER_118"
            scriptContent = "bundle install"
        }
        step {
            id = "RUNNER_117"
            type = "rake-runner"
            param("build-file-path", "Rakefile")
        }
        stepsOrder = arrayListOf("RUNNER_118", "RUNNER_117")
    }

    triggers {
        vcs {
        }
    }
})

object Test : BuildType({
    name = "Test"

    steps {
        step {
            type = "rake-runner"
            param("build-file", """
                task :turn_off_alarm do
                    puts "Turned off alarm. Would have liked 5 more minutes, though."
                  end
                 
                  task :groom_myself do
                    puts "Brushed teeth."
                    puts "Showered."
                    puts "Shaved."
                  end
                 
                  task :make_coffee do
                    cups = ENV["COFFEE_CUPS"] || 2
                    puts "Made #{cups} cups of coffee. Shakes are gone."
                  end
                 
                  task :walk_dog do
                    puts "Dog walked."
                  end
                 
                  task :ready_for_the_day => [:turn_off_alarm, :groom_myself, :make_coffee, :walk_dog] do
                    puts "Ready for the day!"
                  end
            """.trimIndent())
            param("ui.rakeRunner.rake.tasks.names", "ready_for_the_day")
            param("use-custom-build-file", "true")
        }
    }
})

object HttpsGithubComRubyRakeRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/ruby/rake#refs/heads/master"
    url = "https://github.com/ruby/rake"
})

object HttpsGithubComSensuPluginsSensuPluginsDockerRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/sensu-plugins/sensu-plugins-docker#refs/heads/master"
    url = "https://github.com/sensu-plugins/sensu-plugins-docker"
})

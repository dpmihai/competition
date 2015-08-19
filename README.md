#Competition

Current build status: [![Build Status](https://travis-ci.org/dpmihai/competition.png?branch=master)](https://travis-ci.org/dpmihai/competition)

Competition is a web application to create competitions, register users to them and play! This was firstly created for soccer/football competitions with a specific algorithm to give 1 point per game if prognostic (1, X, 2) is guessed, 3 points if score is guessed and at most 3 goals were scored, n points if score is guessed and n goals were scored, where n>3.

##Main Screen

Main screen shows the list of competitions and Top 3. Those finished are seen with a ribbon with the winner's team. Admin user will also see an administration toolbar were he can create users, competitions, teams, stages, games, questions for competitions and do many other things.

![](/docs/screenshots/comp1.png)

##Competition Screen

Any competition has a menu where users can access stages to see scores (theirs, other users scores, real scores), users' top, real competition top, rss news, statistics, graphics, competition's questions with answers and a playoff taken between all registered users.

![](/docs/screenshots/comp2.png)

##Statistics and Graphics

Here users can see the best user's scoring per stage, average points per stage, favorites teams (which brought points). A full report per user can be generated to show scores for all stages. All reports and charts were created using [NextReports](http://www.next-reports.com/).

![](/docs/screenshots/comp3.png)

![](/docs/screenshots/comp4.png)

##More

* Application is developed with Wicket, Spring and NextReports.
* Application automatically notifies users by mail to complete scores in case they forgot.
* See [competitionNotifier](https://github.com/dpmihai/competitionNotifier), an Android application used to connect to a Competition Server and to notify users by SMS.
* For Premier League competition, the real scores are automatically saved by application. (They are taken from http://www.premierleague.com)


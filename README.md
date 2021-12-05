<div id="top"></div>

<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->

[comment]: <> ([![Contributors][contributors-shield]][contributors-url])
[comment]: <> ([![Forks][forks-shield]][forks-url])
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

[comment]: <> ([![LinkedIn][linkedin-shield]][linkedin-url])



<!-- PROJECT LOGO -->
<div align="center">
<pre>
   ___               ___  __  __         _   __ 
  / _ \ ___   ____  / _/ / / / /  ___   (_) / /_
 / ___// -_) / __/ / _/ / /_/ /  / _ \ / / / __/
/_/    \__/ /_/   /_/   \____/  /_//_//_/  \__/ 
</pre>

<h2 align="center">PerfUnit</h2>
  <p align="center">
    take control over application bottlenecks
    <br />
    <br />
    <a href="https://github.com/vitaliy-sk/perfunit">View Demo</a>
    ·
    <a href="https://github.com/vitaliy-sk/perfunit/issues">Report Bug</a>
    ·
    <a href="https://github.com/vitaliy-sk/perfunit/issues">Request Feature</a>
  </p>
</div>


<!-- ABOUT THE PROJECT -->
## About The Project

This is Java Agent which helps to take control over applications bottlenecks, expensive calls or simply control your [performance SLO](https://sre.google/workbook/implementing-slos/) budget.

You can use PerfUnit in your integrations/unit/performance tests in your CI or even in run-time on testing environments.

See an example of usage with JUnit [here](https://github.com/vitaliy-sk/PerfUnit/blob/bf5d3b08f63d512899b4ed2aecda97da1afef3fd/build.gradle#L60) and [here](https://github.com/vitaliy-sk/PerfUnit/blob/master/src/test/java/dev/techh/perfunit/integration/AgentIntegrationTest.java)

#### Key features: 

* Limit method call count per working unit (eg HTTP request, Job run, etc)
* Limit single method call execution time
* Limit total method execution time per working unit
* Log rules violation to the console or markdown report
* Throw an exception if rule violated 

## Getting Started

### Prerequisites

For statistic aggregation PerfUnit uses [Mapped Diagnostic Context (MDC)](http://www.slf4j.org/api/org/slf4j/MDC.html)

You need to put tracing id (for example unique HTTP request ID) to the MDC using any key.

If you use Spring Boot you can do this with [Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth#overview) or [manually](https://medium.com/@d.lopez.j/spring-boot-setting-a-unique-id-per-request-dd648efef2b) 

### Usage

1. Download the latest release from GitHub [release](https://github.com/vitaliy-sk/PerfUnit/releases)
2. Prepare configuration (see sample below)
3. Run your application with PerfUnit agent 

```
-javaagent:/<full_path>/perfunit-1.0.0.jar=/<full_config_path>/perfunit.yml
```

### Configuration sample

```yaml
storageLimit: 1_000_000_000  # Store last 1M calls (default 1M), specify -1 for unlimited storage (may cause out of memory)

reportPath: ./perfunit-report        # Root path for reports (default ./perfunit-report )

reporters:
  saveTraces: true                      # Save thread threads (default: true)
  periodicallySaveReportToDisk: 30000   # Save report each 30 sec (default: -1). It also save report on application exit (enabled by default)

  console:
    enable: true
    printTrace: false                   # Print stack trace when rule is violated (default: false) 

  markdown:
    enable: true

rules:
  # dev.techh.perfunit.integration.service.ExpensiveService - set shared limit to all methods' in the specified class
  # dev.techh.perfunit.integration.service.ExpensiveService#testWithArgs - set shared limit to all methods with name testWithArgs
  # dev.techh.perfunit.integration.service.ExpensiveService#testWithArgs(java.lang.String) - set limit testWithArgs with one String argument
  dev.techh.perfunit.integration.service.ExpensiveService#count5InvocationsAllowed:
     description: Method testNoArgs call to often                # Optional: text description
     allowUnknownCalls: true                                     # Optional: allow invocation without tracing id (default: true)
     allowFail: true                                             # Optional: if "fail" and quota will be reach exception will be thrown  (default: false)
     tracingKey: traceId                                         # Optional: key which be used for tracking quota usage. This key should be present in MDC (default: traceId)
     onlyPublic: true                                            # Optional: instrument only public methods (default: true)
     limit:
         # One or multiple limit can be used at the same time.
         count: 10                                   #  Numbers of call allowed
         timeSingle: 200                             #  Single call limit in msec
         timeTotal: 500                              #  Total time limit in msec
```

<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<!-- LICENSE -->
## License

Distributed under the Apache License Version 2.0 License. See `LICENSE.md` for more information.



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[stars-shield]: https://img.shields.io/github/stars/github_username/repo_name.svg?style=for-the-badge
[stars-url]: https://github.com/vitaliy-sk/perfunit/stargazers
[issues-shield]: https://img.shields.io/github/issues/github_username/repo_name.svg?style=for-the-badge
[issues-url]: https://github.com/vitaliy-sk/perfunit/issues
[license-shield]: https://img.shields.io/github/license/github_username/repo_name.svg?style=for-the-badge
[license-url]: https://github.com/vitaliy-sk/perfunit/blob/master/LICENSE.txt

----

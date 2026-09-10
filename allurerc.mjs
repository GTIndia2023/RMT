export default {
  name: "Optiwise Automation Report",
  // ====== HISTORY CONFIG (very important) ======
  historyPath: "./allure-history/allure-history.jsonl",
  appendHistory: true,

  // ====== KNOWN ISSUES CONFIG (optional but powerful) ======
  knownIssuesPath: "./config/known.json",

  // ====== GLOBAL VARIABLES SHOWN ON TOP ======
  variables: {
    "Project": "OpenCart UI Automation",
    "Framework": "Selenium + TestNG",
  },
  // ====== ENVIRONMENTS GROUPING ======
  environments: {
    staging: {
      matcher: ({ labels }) =>
        labels.some(l => l.name === "env" && l.value === "UAT"),
      variables: {
        "Environment": "UAT",
        "URL":"https://uat.optiwise.wcgt.in/"
      }
    },
    production: {
      matcher: ({ labels }) =>
        labels.some(l => l.name === "env" && l.value === "production"),
      variables: {
        "Environment": "Production",
        "URL":"https://optiwise.wcgt.in/"
      }
    }
  }
};

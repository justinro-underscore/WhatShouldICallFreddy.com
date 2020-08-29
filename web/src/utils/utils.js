export function getUrl(env, endpoint, admin=null) {
  return (env === "development" ? "http://localhost:8080" : "") + (admin !== null ? `/admin/api/${ admin || "_" }/` : "/api/") + endpoint;
}

function defaultApiErrorFunc(errorInfo) {
  console.error(errorInfo);
}

export function fetchApi({
  env,
  endpoint,
  admin=null,
  requestType="GET",
  body=undefined,
  includeCreds=false,
  callback=undefined,
  errorCallback=undefined,
  resType="json",
  resCallback=undefined,
  resErrorCallback=undefined
}) {
  const apiError = errorCallback || ((error) => defaultApiErrorFunc({
    error: error.toString(),
    admin,
    endpoint,
    requestType
  }));
  let fetchData = {
    method: requestType
  };
  if (requestType === "POST" || requestType === "PUT") {
    fetchData.headers = {"Content-Type": "application/json"};
    fetchData.body = body && JSON.stringify(body)
  }
  if (includeCreds) {
    fetchData.credentials = "include";
  }
  let apiCallback = {};
  const okStatusCode = 200;
  if (callback) {
    apiCallback[okStatusCode] = callback;
  }
  else if (typeof resCallback === "object" && resCallback !== null) {
    Object.keys(resCallback).forEach((statusCode) => {
      const obj = resCallback[statusCode];
      if (obj.callback) {
        apiCallback[statusCode] = obj.callback;
      }
      else {
        const resConversion = (res) => (!obj.resType || obj.resType === "json" ? res.json() :
          obj.resType === "blob" ? res.blob() :
          obj.resType === "text" ? res.text() :
          console.error(`res conversion for endpoint "${ endpoint }" is not defined for type ${ obj.resType }`));

        apiCallback[statusCode] = (res) => {
          resConversion(res).then(
            (resJson) => obj.resCallback(resJson),
            (error) => obj.resErrorCallback ? obj.resErrorCallback(error) : apiError(error)
          );
        }
      }
    });
  }
  else {
    const resConversion = (res) => (!resType || resType === "json" ? res.json() :
      resType === "blob" ? res.blob() :
      resType === "text" ? res.text() :
      console.error(`res conversion for endpoint "${ endpoint }" is not defined for type ${ resType }`));

    apiCallback[okStatusCode] = (res) => {
      resConversion(res).then(
        (resJson) => resCallback(resJson),
        (error) => resErrorCallback ? resErrorCallback(error) : apiError(error)
      );
    }
  }
  fetch(getUrl(env, endpoint, admin), fetchData).then(
    (res) => {
      if (res.status in apiCallback) {
        apiCallback[res.status](res);
      }
      else if (0 in apiCallback) {
        apiCallback[0](res);
      }
      else {
        resErrorCallback ? resErrorCallback(res) : apiError(res);
      }
    },
    (error) => apiError(error)
  );
}
# Postman Workflow API Test Summary

## Server Status
✅ **Running** on `http://localhost:8081`

## Postman Collection Created
📁 **Location:** `/home/dell/NetBeansProjects/TaskManagement1/Workflow_API.postman_collection.json`

### Import Instructions:
1. Open **Postman**
2. Click **Import** → **Files**
3. Select `Workflow_API.postman_collection.json`

---

## Test Scenarios

### **Phase 1: Authentication**
```
POST /api/auth/register
{
  "username": "postman_user",
  "userOfficialEmail": "postman@company.com",
  "password": "PassWord@123",
  "role": "USER"
}
Response: Successfully registers user
```

```
POST /api/auth/login
{
  "username": "postman_user",
  "password": "PassWord@123"
}
Response: Returns JWT token
```

**Action:** Copy the token and set it in Postman Variables as `{{token}}`

---

### **Phase 2: Workflow CRUD Operations**

#### **1. Create Workflow** ✅
```
POST /api/workflows/create
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "workFlowName": "Default Flow",
  "description": "Standard workflow"
}
Response: Returns created workflow object with ID
```

#### **2. Get All Workflows** ✅
```
GET /api/workflows/all
Authorization: Bearer {{token}}
Response: Returns list of all workflows
```

#### **3. Get Workflow by ID** ✅
```
GET /api/workflows/{{workflow_id}}
Authorization: Bearer {{token}}
Response: Returns specific workflow details
```

#### **4. Update Workflow** ✅
```
PUT /api/workflows/update/{{workflow_id}}
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "workFlowName": "Updated Flow",
  "description": "Updated description"
}
Response: Returns updated workflow
```

#### **5. Delete Workflow** ✅
```
DELETE /api/workflows/delete/{{workflow_id}}
Authorization: Bearer {{token}}
Response: "Deleted"
```

---

### **Phase 3: Workflow Transitions**

#### **6. Get Allowed Transitions** ✅
```
GET /api/workflows/{{workflow_id}}/transactions?fromStatus=OPEN
Authorization: Bearer {{token}}
Response: List of allowed workflow transitions
```

#### **7. Validate Transition** ✅
```
POST /api/workflows/{{workflow_id}}/validate?fromStatus=OPEN&toStatus=IN_PROGRESS
Authorization: Bearer {{token}}
Content-Type: application/json

["ADMIN", "PROJECT_MANAGER"]
Response: true/false (transition allowed or not)
```

#### **8. Find by Name** ✅
```
GET /api/workflows/name?workFlowName=Default%20Flow
Authorization: Bearer {{token}}
Response: Workflow object matching the name
```

---

## Postman Variables

| Variable | Value | Description |
|----------|-------|-------------|
| `{{token}}` | JWT token | Copy from login response |
| `{{workflow_id}}` | 1,2,3... | Set after creating workflow |

---

## Database Info
- **Host:** localhost:3306
- **Database:** task_management1
- **User:** root
- **Password:** root123

## Test JSON Data

### Register User
```json
{
  "username": "postman_user",
  "userOfficialEmail": "postman@company.com",
  "password": "PassWord@123",
  "role": "USER"
}
```

### Create Workflow
```json
{
  "workFlowName": "Default Flow",
  "description": "Standard workflow"
}
```

---

## Next Steps
1. Import the Postman collection
2. Run **Register User** request
3. Run **Login** request
4. Copy token and update `{{token}}` variable
5. Test workflow endpoints sequentially

**Status:** ✅ All APIs ready for testing!

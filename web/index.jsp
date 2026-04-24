<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String user = (String) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>StudyFlow Pro | Dashboard</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap');
        
        body { 
            font-family: 'Plus Jakarta Sans', sans-serif; 
            background: #0d1117; 
            color: #ffffff; 
            min-height: 100vh; 
            margin: 0;
            display: flex;
        }

        .glass-sidebar { 
            background: #161b22; 
            border-right: 1px solid #30363d; 
            width: 320px; 
            flex-shrink: 0; 
            display: flex; 
            flex-direction: column; 
            padding: 2rem; 
        }

        .main-content { 
            flex-grow: 1; 
            overflow-y: auto; 
            padding: 2.5rem; 
        }

        .input-dark { 
            background: #0d1117; 
            border: 1px solid #30363d; 
            outline: none; 
            color: white; 
            padding: 0.85rem; 
            border-radius: 0.75rem; 
            width: 100%; 
            margin-bottom: 1rem;
        }

        .calendar-grid { 
            display: grid; 
            grid-template-columns: repeat(7, 1fr); 
            gap: 8px; 
            background: #161b22; 
            padding: 1.5rem;
            border-radius: 1.5rem; 
            border: 1px solid #30363d; 
        }

        .calendar-day { 
            background: #0d1117; 
            border: 1px solid #30363d;
            border-radius: 0.75rem;
            padding: 0.5rem;
            min-height: 100px;
            display: flex;
            flex-direction: column;
            cursor: pointer;
            transition: 0.2s;
        }
        .calendar-day:hover { border-color: #5833ff; background: #1a1f2e; }
        
        .day-number {
            font-size: 0.9rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
            color: #9ca3af;
        }
        
        .task-tag-cal {
            font-size: 0.7rem;
            background: #5833ff;
            color: white;
            padding: 0.2rem 0.5rem;
            border-radius: 999px;
            margin-bottom: 0.25rem;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .deadline-card {
            background: #161b22;
            border: 1px solid #30363d;
            border-radius: 1.25rem;
            padding: 1.5rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
            transition: transform 0.2s;
        }

        .deadline-card:hover { transform: translateY(-2px); }

        .status-btn {
            font-weight: 700;
            font-size: 0.85rem;
            padding: 0.75rem 1.75rem;
            border-radius: 0.85rem;
            min-width: 130px;
            text-align: center;
            transition: opacity 0.2s;
        }

        .status-btn.pending {
            background: #5833ff;
            color: white;
            box-shadow: 0 4px 15px rgba(88, 51, 255, 0.2);
        }

        .status-btn.completed {
            background: #238636;
            color: white;
        }

        .btn-primary {
            background: #5833ff;
            width: 100%;
            padding: 1rem;
            border-radius: 1rem;
            font-weight: 700;
            margin-top: 0.5rem;
        }
    </style>
</head>
<body>

    <aside class="glass-sidebar">
        <div class="flex items-center gap-4 mb-10">
            <div class="w-10 h-10 bg-indigo-600 rounded-xl flex items-center justify-center text-white"><i class="fas fa-layer-group"></i></div>
            <h1 class="text-xl font-bold">StudyFlow <span class="text-indigo-500">PRO</span></h1>
        </div>

        <div class="flex-grow">
            <h3 class="text-xs font-bold text-gray-500 uppercase tracking-widest mb-4">New Assignment</h3>
            <form onsubmit="event.preventDefault(); saveTask();">
                <input type="text" id="t-subject" class="input-dark" placeholder="Task name" required>
                <input type="date" id="t-date" class="input-dark" required>
                <input type="time" id="t-time" class="input-dark" required>
                <button type="submit" class="btn-primary">Save Task</button>
            </form>

            <div class="mt-10">
                <h3 class="text-xs font-bold text-gray-500 uppercase tracking-widest mb-4">Pending Alarms</h3>
                <div id="alarm-list" class="space-y-3"></div>
            </div>
        </div>
        <h3 class="text-xs font-bold text-gray-500 uppercase tracking-widest mt-6">Study Plans</h3>
<div id="plan-list" class="space-y-2"></div>

<h3 class="text-xs font-bold text-gray-500 uppercase tracking-widest mt-6">Time Logs</h3>
<div id="time-list" class="space-y-2"></div>

        <div class="pt-6 border-t border-gray-800 flex justify-between items-center">
            <span class="text-sm font-semibold text-gray-400"><%= user %></span>
            <button onclick="location.href='LogoutServlet'" class="text-gray-500 hover:text-red-500"><i class="fas fa-sign-out-alt"></i></button>
        </div>
    </aside>

    <main class="main-content">
        <div class="max-w-5xl mx-auto space-y-10">
            <div>
                <h2 class="text-3xl font-extrabold mb-6">Study Calendar</h2>
                <div class="calendar-grid" id="grid-root"></div>
            </div>

            <div>
                <h2 class="text-xl font-bold uppercase tracking-wider text-gray-400 mb-6">Upcoming Deadlines</h2>
                <div id="deadline-list"></div>
            </div>
        </div>
    </main>

    <script>
        let activeTasks = [];

        window.onload = loadData;

function loadData() {
    fetch("GetTopicsServlet?t=" + new Date().getTime())
        .then(r => r.json())
        .then(data => {
            activeTasks = data;
            renderUI();

            // ✅ NEW FEATURES
            loadPlans();
            loadTimeLogs();
            loadReminders();
        });
}
function loadPlans(){
    fetch("GetPlansServlet")
    .then(res=>res.json())
    .then(data=>{
        let box=document.getElementById("plan-list");
        box.innerHTML="";

        if(data.length===0){
            box.innerHTML="No Plans";
            return;
        }

        data.forEach(p=>{
            box.innerHTML+=`
            <div class="bg-[#0d1117] p-2 rounded border border-[#30363d]">
                📘 ${p.title} (${p.date})
            </div>`;
        });
    });
}
function loadPlans(){
    fetch("GetPlansServlet")
    .then(res=>res.json())
    .then(data=>{
        let box=document.getElementById("plan-list");
        box.innerHTML="";

        if(data.length===0){
            box.innerHTML="No Plans";
            return;
        }

        data.forEach(p=>{
            box.innerHTML+=`
            <div class="bg-[#0d1117] p-2 rounded border border-[#30363d]">
                📘 ${p.title} (${p.date})
            </div>`;
        });
    });
}
function loadTimeLogs(){
    fetch("GetTimeLogsServlet")
    .then(res=>res.json())
    .then(data=>{
        let box=document.getElementById("time-list");
        box.innerHTML="";

        if(data.length===0){
            box.innerHTML="No Time Logs";
            return;
        }

        data.forEach(t=>{
            box.innerHTML+=`
            <div class="bg-[#0d1117] p-2 rounded border border-[#30363d] text-xs">
                ⏱ ${t.hours} hrs (${t.date})
            </div>`;
        });
    });
}
function loadReminders(){
    fetch("GetRemindersServlet")
    .then(res=>res.json())
    .then(data=>{
        let box=document.getElementById("alarm-list");
        box.innerHTML="";

        if(data.length===0){
            box.innerHTML='<div class="text-gray-500 text-sm">✨ No reminders</div>';
            return;
        }

        data.forEach(r=>{
            box.innerHTML+=`
            <div class="bg-[#0d1117] p-3 rounded-lg border border-[#30363d]">
                🔔 ${r.next_review_date}
            </div>`;
        });
    });
}
        function renderUI() {
            // ---------- 1. CALENDAR: show only PENDING tasks ----------
            const root = document.getElementById('grid-root');
            root.innerHTML = "";
            
            const today = new Date();
            const currentYear = today.getFullYear();
            const currentMonth = today.getMonth();
            
            const firstDayOfMonth = new Date(currentYear, currentMonth, 1);
            const startWeekday = firstDayOfMonth.getDay();
            const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
            
            let calendarHTML = '';
            let dayCounter = 1;
            
            for (let i = 0; i < 42; i++) {
                if (i < startWeekday || dayCounter > daysInMonth) {
                    calendarHTML += '<div class="calendar-day opacity-30"><div class="day-number">-</div></div>';
                    if (i >= startWeekday) dayCounter++;
                } else {
                    const dateStr = currentYear + '-' + String(currentMonth+1).padStart(2,'0') + '-' + String(dayCounter).padStart(2,'0');
                    // ***** KEY CHANGE: filter only pending tasks (status !== 'Completed') *****
                    const tasksForDay = activeTasks.filter(t => t.due_date === dateStr && t.status !== 'Completed');
                    
                    let tasksHTML = '';
                    tasksForDay.forEach(task => {
                        tasksHTML += '<div class="task-tag-cal" title="' + (task.subject + ' - ' + (task.time || '')) + '">📌 ' + escapeHtml(task.subject) + '</div>';
                    });
                    
                    calendarHTML += '<div class="calendar-day">' +
                                    '<div class="day-number">' + dayCounter + '</div>' +
                                    '<div class="overflow-y-auto max-h-16">' + tasksHTML + '</div>' +
                                    '</div>';
                    dayCounter++;
                }
            }
            root.innerHTML = calendarHTML;
            
            // ---------- 2. PENDING ALARMS (sidebar) ----------
            const alarmList = document.getElementById('alarm-list');
            alarmList.innerHTML = "";
            const pendingAlarms = activeTasks.filter(t => t.status !== 'Completed');
            if (pendingAlarms.length === 0) {
                alarmList.innerHTML = '<div class="text-gray-500 text-sm">✨ No pending alarms</div>';
            } else {
                pendingAlarms.forEach(t => {
                    alarmList.innerHTML += '<div class="bg-[#0d1117] p-3 rounded-lg border border-[#30363d]">' +
                                            '<p class="text-sm font-bold">' + escapeHtml(t.subject) + '</p>' +
                                            '<p class="text-[10px] text-gray-500">' + t.due_date + ' ' + (t.time || '') + '</p>' +
                                            '</div>';
                });
            }
            
            // ---------- 3. UPCOMING DEADLINES (all tasks, with toggle) ----------
            const deadlineList = document.getElementById('deadline-list');
            deadlineList.innerHTML = "";
            activeTasks.forEach(t => {
                const isDone = t.status === 'Completed';
                deadlineList.innerHTML += '<div class="deadline-card">' +
                                            '<div>' +
                                                '<h4 class="text-xl font-bold text-white mb-1">' + escapeHtml(t.subject) + '</h4>' +
                                                '<p class="text-gray-500 font-medium">' + t.due_date + (t.time ? ' at ' + t.time : '') + '</p>' +
                                            '</div>' +
                                            '<button onclick="toggleStatus(' + t.id + ', \'' + t.status + '\')" class="status-btn ' + (isDone ? 'completed' : 'pending') + '">' +
                                                (isDone ? 'Completed' : 'Pending') +
                                            '</button>' +
                                        '</div>';
            });
        }

        function saveTask() {
            const sub = document.getElementById('t-subject').value.trim();
            const dat = document.getElementById('t-date').value;
            const tim = document.getElementById('t-time').value;
            if (!sub || !dat || !tim) {
                alert("Please fill all fields");
                return;
            }
            
            fetch("AddTopicServlet", {
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: "subject=" + encodeURIComponent(sub) + "&topic=Task&due_date=" + dat + "&time=" + tim
            }).then(() => {
                document.getElementById('t-subject').value = "";
                loadData();
            }).catch(err => console.error("Save error:", err));
        }

        function toggleStatus(id, status) {
            const newStatus = status === "Completed" ? "Pending" : "Completed";
            fetch("UpdateStatusServlet", {
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: "id=" + id + "&status=" + newStatus
            }).then(() => loadData());
        }
        
        function escapeHtml(str) {
            if (!str) return '';
            return str.replace(/[&<>]/g, function(m) {
                if (m === '&') return '&amp;';
                if (m === '<') return '&lt;';
                if (m === '>') return '&gt;';
                return m;
            });
        }
    </script>
</body>
</html>
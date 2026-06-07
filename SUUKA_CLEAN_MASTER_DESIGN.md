# SUUKA CLEAN MASTER DESIGN & DEVELOPMENT INSTRUCTIONS

## SYSTEM OBJECTIVE
Design and build a professional, enterprise-grade cleaning marketplace platform called **Suuka Clean**.

The platform must look like a commercial SaaS product, not a student project.

Design inspiration:

- Uber
- Airbnb
- Stripe
- Linear
- Notion
- Monday.com

The platform shall support:

1. Clients
2. Cleaners
3. Supervisors
4. Administrators

Every role must have different permissions, layouts, dashboards, and AI capabilities.

No two roles should have the same access rights.

Administrators must have the highest level of visibility and control.

---

# MOST IMPORTANT DESIGN RULE
The entire system must use a single visual design language.

The application must feel like one unified product.

Never create pages that look different from one another.

Every page must follow the same:

- Spacing
- Card styles
- Form styles
- Border radius
- Color system
- Typography
- Navigation pattern

---

# COLOR SYSTEM
Application Background

#F5F6F8

This color must cover:

- Dashboards
- Analytics
- Forms
- Settings
- Tables
- Reports

Card Background

#FFFFFF

Primary Color

#10B981

Border Color

#E5E7EB

Primary Text

#111827

Secondary Text

#6B7280

Do not introduce additional brand colors.

Avoid colorful dashboards.

Maintain a clean professional appearance.

---

# TYPOGRAPHY
Font Family

Inter

Page Titles

32px
Bold

Section Titles

24px
Semi Bold

Card Titles

16px
Medium

Body Text

14px

Table Text

14px

---

# GLOBAL APPLICATION LAYOUT
Desktop

Sidebar Width

280px

Top Navigation Height

72px

Content Padding

32px

Section Gap

24px

Maximum Content Width

1600px

Centered Layout

Mobile

Sidebar becomes drawer.

Bottom navigation appears.

Maximum five items.

---

# PAGE STRUCTURE
Every page must follow this exact structure.

1. Page Header
2. KPI Cards
3. AI Insights (if permitted)
4. Quick Actions
5. Main Content
6. Activity Feed

Never change this structure.

---

# CARD DESIGN
Every card in the system must use:

Width

Responsive

Height

Auto

Padding

24px

Radius

16px

Border

1px solid #E5E7EB

Background

White

Hover Effect

Subtle elevation

No strong shadows.

---

# FORM DESIGN RULES
All forms must use the same component library.

Small Form Width

600px

Medium Form Width

800px

Large Form Width

1000px

Wizard Width

1200px

Centered on page.

Input Height

48px

Text Area Minimum Height

120px

Field Gap

24px

Label Gap

8px

Buttons Height

48px

Button Radius

12px

---

# TABLE DESIGN RULES
All tables must be inside white cards.

Features Required:

Search

Filter

Sorting

Pagination

Export CSV

Export PDF

Bulk Actions

Row Height

60px

Header Height

56px

---

# ROLE-BASED ACCESS CONTROL

## CLIENT
Purpose

Request and manage cleaning services.

Can Access

Dashboard

Bookings

Invoices

Wallet

Reviews

Messages

Support

Profile

Cannot Access

Analytics

Inventory

Cleaner Management

Reports

AI Intelligence Center

Admin Features

---

## CLEANER
Purpose

Manage assigned jobs.

Can Access

Dashboard

Jobs

Schedule

Route Planner

Earnings

Supply Requests

Messages

Profile

Cannot Access

Admin Features

Client Lists

Analytics

Inventory Management

Financial Reports

AI Control Features

---

## SUPERVISOR
Purpose

Monitor operational teams.

Can Access

Cleaner Monitoring

Performance Reports

Complaint Monitoring

Territory Management

Supply Approval

Cannot Access

System Settings

Finance Management

AI Configuration

Critical Administrative Controls

---

## ADMIN
Purpose

Operate entire business.

Can Access Everything.

Only Admins Can:

Approve AI recommendations

Manage inventory

Manage users

Manage finances

View analytics

Configure system

View audit logs

Manage AI settings

Approve critical actions

---

# AI INTEGRATION RULES
AI must be integrated throughout the system.

AI is an assistant.

AI is never a decision maker.

AI cannot:

Approve

Reject

Suspend

Delete

Refund

Purchase

Pay

Terminate

Modify settings

All critical actions require human approval.

---

# CLIENT AI FEATURES
Location

Below KPI Cards.

Height

220px

Width

100%

Functions

Booking Suggestions

Cleaner Recommendations

Cleaning Reminders

Loyalty Recommendations

AI only recommends.

Client decides.

---

# CLEANER AI FEATURES
Location

Below KPI Cards.

Height

250px

Width

100%

Functions

Route Optimization

Schedule Optimization

Supply Suggestions

Performance Insights

Cleaner decides.

---

# SUPERVISOR AI FEATURES
Functions

Performance Monitoring

Coverage Analysis

Complaint Detection

Training Recommendations

Supervisor decides.

---

# ADMIN AI COMMAND CENTER
This is the most important AI module.

Location

Separate menu item:

AI Intelligence

Layout

Executive Overview

AI Alerts

Recommendations Queue

Forecasting

Risk Detection

Audit Logs

AI Recommendations Table Height

600px

Scrollable

---

# AI DETECTIONS
AI must continuously analyze:

Inactive Clients

High Value Clients

Customer Churn Risk

Complaint Trends

Revenue Trends

Inventory Depletion

Supply Waste

Cleaner Performance

Service Gaps

Demand Forecasting

Operational Risks

Fraud Indicators

Missed Follow-Ups

Low Customer Engagement

Geographic Expansion Opportunities

---

# AI RECOMMENDATION WORKFLOW
Step 1

AI analyzes data.

Step 2

AI generates recommendation.

Step 3

Recommendation appears in dashboard.

Step 4

Authorized user reviews recommendation.

Step 5

User chooses:

Approve

Ignore

Schedule

Escalate

Step 6

System records action.

Step 7

Audit log updated.

---

# ADMIN DASHBOARD LAYOUT
Row 1

Executive KPI Cards

Revenue

Bookings

Customer Satisfaction

Cleaner Utilization

---
Row 2

AI Intelligence Summary

Customers At Risk

Service Gaps

Inventory Alerts

Performance Issues

---
Row 3

Operations Board

Pending

Assigned

Active

Completed

Kanban Layout

---
Row 4

AI Recommendation Queue

Scrollable

Height

600px

---
Row 5

Analytics & Forecasts

Revenue Trends

Demand Forecast

Cleaner Performance

Customer Retention

---
Row 6

Audit Logs

AI Activities

Admin Decisions

System Events

---

# INVENTORY MODULE
Must Include

Stock Levels

Reorder Thresholds

Supplier Database

Purchase Requests

Inventory Forecasting

AI Stock Recommendations

Admin Approval Workflow

---

# ANALYTICS MODULE
Must Include

Revenue Analytics

Customer Analytics

Cleaner Analytics

Inventory Analytics

Operational Analytics

AI Forecasting

Date Filters

7 Days

30 Days

90 Days

Custom

---

# RESPONSIVE DESIGN
Desktop

Full Sidebar

Tablet

Collapsed Sidebar

Mobile

Drawer Navigation

Bottom Navigation

Cards Stack Vertically

Tables Convert To Cards

Forms Become Full Width

---

# DEVELOPMENT RULES
Never use emojis.

Use Font Awesome or Lucide icons.

Use reusable components.

Avoid duplicated code.

Use role-based rendering.

Use modular architecture.

Implement loading states.

Implement empty states.

Implement error states.

Implement audit logging.

Implement permission checking on every page.

Implement AI recommendation approval workflows.

Ensure the UI looks identical across all modules.

The final product should feel like an enterprise operations platform capable of managing a multi-city cleaning business with AI-assisted intelligence and strict human oversight.

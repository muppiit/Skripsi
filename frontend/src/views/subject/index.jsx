import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  Button,
  Card,
  Col,
  Divider,
  Input,
  message,
  Modal,
  Row,
  Select,
  Skeleton,
  Table,
} from "antd";
import { DeleteOutlined, EditOutlined } from "@ant-design/icons";
import {
  addSubject,
  deleteSubject,
  editSubject,
  getSubjects,
} from "@/api/subject";
import { getStudyPrograms } from "@/api/studyProgram";
import { getSubjectGroups } from "@/api/subjectGroup";
import TypingCard from "@/components/TypingCard";
import { useTableSearch } from "@/helper/tableSearchHelper.jsx";
import AddSubjectForm from "./forms/add-mapel-form";
import EditSubjectForm from "./forms/edit-mapel-form";

const Subject = () => {
  const [subjects, setSubjects] = useState([]);
  const [studyPrograms, setStudyPrograms] = useState([]);
  const [subjectGroups, setSubjectGroups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedStudyProgram, setSelectedStudyProgram] = useState(null);
  const [selectedSubjectGroup, setSelectedSubjectGroup] = useState(null);
  const [addModalVisible, setAddModalVisible] = useState(false);
  const [addModalLoading, setAddModalLoading] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editModalLoading, setEditModalLoading] = useState(false);
  const [currentRowData, setCurrentRowData] = useState({});

  const { getColumnSearchProps } = useTableSearch();

  const fetchSubjects = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getSubjects();
      if (result.data.statusCode === 200) {
        setSubjects(result.data.content || []);
      } else {
        message.error("Gagal mengambil data mata kuliah");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchRelations = useCallback(async () => {
    try {
      const [studyProgramResult, subjectGroupResult] = await Promise.all([
        getStudyPrograms(),
        getSubjectGroups(),
      ]);

      if (studyProgramResult.data.statusCode === 200) {
        setStudyPrograms(studyProgramResult.data.content || []);
      }

      if (subjectGroupResult.data.statusCode === 200) {
        setSubjectGroups(subjectGroupResult.data.content || []);
      }
    } catch (error) {
      message.error("Gagal memuat data relasi mata kuliah");
    }
  }, []);

  useEffect(() => {
    fetchSubjects();
    fetchRelations();
  }, [fetchRelations, fetchSubjects]);

  const filteredSubjects = useMemo(() => {
    const keyword = searchQuery.trim().toLowerCase();

    return subjects.filter((subject) => {
      const matchStudyProgram = selectedStudyProgram
        ? subject.studyProgram?.id === selectedStudyProgram ||
          subject.study_program?.id === selectedStudyProgram
        : true;
      const matchSubjectGroup = selectedSubjectGroup
        ? subject.subject_group?.id === selectedSubjectGroup
        : true;
      const matchSearch = keyword
        ? [
            subject.name,
            subject.description,
            subject.studyProgram?.name,
            subject.study_program?.name,
            subject.subject_group?.name,
            subject.credit_point,
            subject.year_commenced,
          ]
            .filter(Boolean)
            .some((value) => String(value).toLowerCase().includes(keyword))
        : true;

      return matchStudyProgram && matchSubjectGroup && matchSearch;
    });
  }, [searchQuery, selectedStudyProgram, selectedSubjectGroup, subjects]);

  const handleAddSubjectOk = async (values) => {
    setAddModalLoading(true);
    try {
      await addSubject({
        name: values.name,
        description: values.description,
        credit_point: Number(values.credit_point),
        year_commenced: Number(values.year_commenced),
        study_program_id: values.study_program_id,
        subject_group_id: values.subject_group_id,
      });
      setAddModalVisible(false);
      message.success("Mata kuliah berhasil ditambahkan");
      fetchSubjects();
    } catch (error) {
      message.error("Gagal menambahkan mata kuliah: " + error.message);
    } finally {
      setAddModalLoading(false);
    }
  };

  const handleEditSubjectOk = async (values) => {
    setEditModalLoading(true);
    try {
      await editSubject(
        {
          name: values.name,
          description: values.description,
          credit_point: Number(values.credit_point),
          year_commenced: Number(values.year_commenced),
          study_program_id: values.study_program_id,
          subject_group_id: values.subject_group_id,
        },
        currentRowData.id
      );
      setEditModalVisible(false);
      message.success("Mata kuliah berhasil diperbarui");
      fetchSubjects();
    } catch (error) {
      message.error("Gagal mengubah mata kuliah: " + error.message);
    } finally {
      setEditModalLoading(false);
    }
  };

  const handleDeleteSubject = (row) => {
    Modal.confirm({
      title: "Konfirmasi",
      content: "Apakah Anda yakin ingin menghapus mata kuliah ini?",
      okText: "Ya",
      okType: "danger",
      cancelText: "Tidak",
      onOk: async () => {
        try {
          await deleteSubject({ id: row.id });
          message.success("Mata kuliah berhasil dihapus");
          fetchSubjects();
        } catch (error) {
          message.error("Gagal menghapus mata kuliah: " + error.message);
        }
      },
    });
  };

  const columns = [
    {
      title: "No",
      key: "index",
      align: "center",
      width: 70,
      render: (_, __, index) => index + 1,
    },
    {
      title: "Nama Mata Kuliah",
      dataIndex: "name",
      key: "name",
      ...getColumnSearchProps("name"),
      sorter: (a, b) => (a.name || "").localeCompare(b.name || ""),
    },
    {
      title: "Prodi",
      key: "study_program",
      render: (_, row) => row.studyProgram?.name || row.study_program?.name || "-",
      sorter: (a, b) =>
        (a.studyProgram?.name || a.study_program?.name || "").localeCompare(
          b.studyProgram?.name || b.study_program?.name || ""
        ),
    },
    {
      title: "Rumpun",
      dataIndex: ["subject_group", "name"],
      key: "subject_group",
      render: (value) => value || "-",
      sorter: (a, b) =>
        (a.subject_group?.name || "").localeCompare(b.subject_group?.name || ""),
    },
    {
      title: "SKS",
      dataIndex: "credit_point",
      key: "credit_point",
      align: "center",
      sorter: (a, b) => (a.credit_point || 0) - (b.credit_point || 0),
    },
    {
      title: "Tahun Mulai",
      dataIndex: "year_commenced",
      key: "year_commenced",
      align: "center",
      sorter: (a, b) => (a.year_commenced || 0) - (b.year_commenced || 0),
    },
    {
      title: "Deskripsi",
      dataIndex: "description",
      key: "description",
      ellipsis: true,
      render: (value) => value || "-",
    },
    {
      title: "Operasi",
      key: "action",
      align: "center",
      width: 140,
      render: (_, row) => (
        <span>
          <Button
            type="primary"
            shape="circle"
            icon={<EditOutlined />}
            onClick={() => {
              setCurrentRowData(row);
              setEditModalVisible(true);
            }}
          />
          <Divider type="vertical" />
          <Button
            type="primary"
            danger
            shape="circle"
            icon={<DeleteOutlined />}
            onClick={() => handleDeleteSubject(row)}
          />
        </span>
      ),
    },
  ];

  const cardContent =
    "Di sini, Anda dapat mengelola mata kuliah sesuai program studi dan rumpun mata kuliah.";

  return (
    <div className="app-container">
      <TypingCard title="Manajemen Mata Kuliah" source={cardContent} />
      <br />
      {loading ? (
        <Card>
          <Skeleton active paragraph={{ rows: 10 }} />
        </Card>
      ) : (
        <Card style={{ overflowX: "auto" }}>
          <Row gutter={[16, 16]} justify="space-between" align="middle">
            <Col>
              <Row gutter={[12, 12]}>
                <Col>
                  <Button type="primary" onClick={() => setAddModalVisible(true)}>
                    Tambahkan Mata Kuliah
                  </Button>
                </Col>
                <Col>
                  <Select
                    placeholder="Filter Prodi"
                    value={selectedStudyProgram}
                    style={{ width: 220 }}
                    onChange={setSelectedStudyProgram}
                    allowClear
                  >
                    {studyPrograms.map((program) => (
                      <Select.Option key={program.id} value={program.id}>
                        {program.name}
                      </Select.Option>
                    ))}
                  </Select>
                </Col>
                <Col>
                  <Select
                    placeholder="Filter Rumpun"
                    value={selectedSubjectGroup}
                    style={{ width: 220 }}
                    onChange={setSelectedSubjectGroup}
                    allowClear
                  >
                    {subjectGroups.map((group) => (
                      <Select.Option key={group.id} value={group.id}>
                        {group.name}
                      </Select.Option>
                    ))}
                  </Select>
                </Col>
              </Row>
            </Col>
            <Col>
              <Input.Search
                placeholder="Cari mata kuliah, prodi, rumpun..."
                allowClear
                enterButton
                value={searchQuery}
                onChange={(event) => setSearchQuery(event.target.value)}
                onSearch={setSearchQuery}
                style={{ width: 360 }}
              />
            </Col>
          </Row>
          <br />
          <Table
            rowKey="id"
            dataSource={filteredSubjects}
            columns={columns}
            pagination={{ pageSize: 10 }}
          />

          <AddSubjectForm
            visible={addModalVisible}
            confirmLoading={addModalLoading}
            onCancel={() => setAddModalVisible(false)}
            onOk={handleAddSubjectOk}
            studyPrograms={studyPrograms}
            subjectGroups={subjectGroups}
          />

          <EditSubjectForm
            currentRowData={currentRowData}
            visible={editModalVisible}
            confirmLoading={editModalLoading}
            onCancel={() => setEditModalVisible(false)}
            onOk={handleEditSubjectOk}
            studyPrograms={studyPrograms}
            subjectGroups={subjectGroups}
          />
        </Card>
      )}
    </div>
  );
};

export default Subject;
